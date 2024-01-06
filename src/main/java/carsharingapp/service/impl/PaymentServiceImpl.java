package carsharingapp.service.impl;

import carsharingapp.dto.payment.CreatePaymentRequestDto;
import carsharingapp.dto.payment.PaymentDto;
import carsharingapp.exception.EntityNotFoundException;
import carsharingapp.mapper.PaymentMapper;
import carsharingapp.model.Car;
import carsharingapp.model.Payment;
import carsharingapp.model.Rental;
import carsharingapp.repository.PaymentRepository;
import carsharingapp.repository.RentalRepository;
import carsharingapp.service.NotificationService;
import carsharingapp.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final double FINE_MULTIPLIER = 1.5;
    private static final String DOMAIN = "http://localhost:8080";
    private static final String SUCCESSFUL_URL = "/payments/success?sessionId={CHECKOUT_SESSION_ID}";
    private static final String CANCELED_URL = "/payments/cancel?sessionId={CHECKOUT_SESSION_ID}";

    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;

    @Value("${secret.stripe.key}")
    private String stripeKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    @Override
    public List<PaymentDto> getPayments(Long userId, Pageable pageable) {
        return paymentRepository.findAllByRentalUserId(userId, pageable)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentDto> getPaymentsByStatus(Long userId, String status, Pageable pageable) {
        return paymentRepository.findAllByStatus(Payment.PaymentStatus.valueOf(status.toUpperCase()))
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentDto createPaymentSession(CreatePaymentRequestDto requestDto) {
        Optional<Payment> dbPayment = paymentRepository.findAllByRentalId(requestDto.rentalId())
                .stream()
                .filter(payment -> payment.getType() == requestDto.paymentType())
                .filter(payment -> payment.getStatus() != Payment.PaymentStatus.CANCELED)
                .findFirst();

        if (dbPayment.isPresent()) {
            Payment payment = dbPayment.get();
            if (payment.getStatus() == Payment.PaymentStatus.PAID) {
                throw new EntityNotFoundException("This rent has been already paid");
            }
            if (payment.getStatus() == Payment.PaymentStatus.PENDING) {
                return paymentMapper.toDto(payment);
            }
        }

        Payment payment = new Payment();
        Rental rental = rentalRepository.findById(requestDto.rentalId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental by id: "
                        + requestDto.rentalId()));
        payment.setRental(rental);
        payment.setType(requestDto.paymentType());

        Car car = rental.getCar();
        BigDecimal dailyFee = car.getDailyFee();
        long days;
        if (requestDto.paymentType() == Payment.PaymentType.PAYMENT) {
            LocalDateTime rentalDateTime = rental.getRentalDateTime();
            LocalDateTime returnDateTime = rental.getReturnDateTime();
            Duration duration = Duration.between(rentalDateTime, returnDateTime);
            days = duration.toDays() + 1;
        } else {
            LocalDateTime returnDateTime = rental.getReturnDateTime();
            LocalDateTime actualReturnDateTime = rental.getActualReturnDateTime();
            Duration duration = Duration.between(returnDateTime, actualReturnDateTime);
            dailyFee = dailyFee.multiply(BigDecimal.valueOf(FINE_MULTIPLIER));
            days = duration.toDays() + 1;
        }

        BigDecimal price = dailyFee.multiply(BigDecimal.valueOf(days));
        payment.setPrice(price);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment = checkout(car, payment);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    private Payment checkout(Car car, Payment payment) {
        SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setExpiresAt(Instant.now().plus(31, ChronoUnit.MINUTES)
                        .getEpochSecond()).addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(payment.getPrice().longValue() * 100L)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                        .builder()
                                        .setName("Renting: " + car.getBrand()
                                        + " " + car.getModel())
                                        .addImage("https://example.com/image.jpg")
                                        .build())
                                .build()
                        ).setQuantity(1L)
                        .build()).setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(DOMAIN + SUCCESSFUL_URL).setCancelUrl(DOMAIN + CANCELED_URL);
        Session session;
        try {
            session = Session.create(builder.build());
            payment.setSessionId(session.getId());
            payment.setSessionUrl(new URL(session.getUrl()));
        } catch (StripeException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return payment;
    }

    @Override
    @Transactional
    public PaymentDto getSuccessfulPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new RuntimeException("Invalid session id: " + sessionId));
        payment.setStatus(Payment.PaymentStatus.PAID);
        notificationService.sendMessageAboutSuccessPayment(payment, payment.getRental().getCar());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto getCancelledPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new RuntimeException("Invalid session id: " + sessionId));
        payment.setStatus(Payment.PaymentStatus.CANCELED);
        notificationService.sendMessageAboutCanceledPayment(payment, payment.getRental().getCar());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }
}
