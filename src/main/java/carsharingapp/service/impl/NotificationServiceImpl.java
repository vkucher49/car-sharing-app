package carsharingapp.service.impl;

import carsharingapp.model.Car;
import carsharingapp.model.Payment;
import carsharingapp.model.Rental;
import carsharingapp.service.NotificationService;
import carsharingapp.tg.CarSharingBot;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private static final DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CarSharingBot carSharingBot;

    @Override
    public void sendMessageAboutCreatedRental(Rental rental) {
        String car = formatCar(rental.getCar());
        String date = formatDate(rental.getRentalDateTime(), rental.getReturnDateTime());
        String name = "Dear, %s".formatted(rental.getUser().getFirstName());
        String message = name + System.lineSeparator() + "You rent car: "
                + car + System.lineSeparator()
                + date;
        carSharingBot.sendMessage(message);
    }

    @Override
    public void sendMessageAboutOverdueRental(Rental rental) {
        Car car = rental.getCar();
        String message = """
                We hope you are enjoying your rental experience.
                However, we noticed that the return date for your rented car has passed.
                You should have returned car: %s, on %s.
                """.formatted(formatCar(car), formatOverdueRental(rental.getReturnDateTime(),
                rental.getActualReturnDateTime()));
        carSharingBot.sendMessage(message);
    }

    @Override
    public void sendMessageAboutSuccessPayment(Payment payment, Car car) {
        String message = """
                Payment was successful!
                Car model: %s,
                Sum: %s.
                """.formatted(formatCar(car), payment.getPrice());
        carSharingBot.sendMessage(message);
    }

    @Override
    public void sendMessageAboutCanceledPayment(Payment payment, Car car) {
        String message = """
                Your payment for car: %s, is failed, try again.
                
                Total bill: %sUSD
                """.formatted(formatCar(car), payment.getPrice());
        carSharingBot.sendMessage(message);
    }

    @Override
    public void sendMessageAboutOverdueRentals(Set<Rental> overdueRentals) {
        String message = """
                You have overdue rentals, please don't forget to return it: %s.
                """.formatted(formatRentals(overdueRentals));
        carSharingBot.sendMessage(message);
    }

    public String formatCar(Car car) {
        return """
                model -> %s,
                brand -> %s,
                type -> %s""".formatted(car.getModel(), car.getBrand(), car.getCarType().name());
    }

    public String formatDate(LocalDateTime rentalDateTime, LocalDateTime returnDateTime) {
        String formattedRentalTime = rentalDateTime.format(formatter);
        String formattedReturnTime = returnDateTime.format(formatter);
        return """
                Your rent date and time is: %s,
                return date is: %s,
                If the car is not returned promptly, you will be punished with a fine.
                """.formatted(formattedRentalTime, formattedReturnTime);
    }

    private String formatOverdueRental(LocalDateTime returnDateTime,
                                       LocalDateTime actualReturnDateTime) {
        String formattedReturnTime = returnDateTime.format(formatter);
        String formattedActualReturnTime = actualReturnDateTime.format(formatter);
        return """
                %s,
                but you returned it on %s.
                """.formatted(formattedReturnTime, formattedActualReturnTime);
    }

    public String formatRentals(Set<Rental> overdueRentals) {
        StringBuilder builder = new StringBuilder();
        for (Rental rental : overdueRentals) {
            builder.append("Car: ")
                    .append(formatCar(rental.getCar()));
        }
        return builder.toString();
    }
}
