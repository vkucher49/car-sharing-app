package carsharingapp.service;

import carsharingapp.dto.payment.CreatePaymentRequestDto;
import carsharingapp.dto.payment.PaymentDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    List<PaymentDto> getPayments(Long userId, Pageable pageable);

    List<PaymentDto> getPaymentsByStatus(Long userId, String status, Pageable pageable);

    PaymentDto createPaymentSession(CreatePaymentRequestDto requestDto);

    PaymentDto getSuccessfulPayment(String sessionId);

    PaymentDto getCancelledPayment(String sessionId);
}
