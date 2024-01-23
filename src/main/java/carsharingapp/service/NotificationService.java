package carsharingapp.service;

import carsharingapp.model.Car;
import carsharingapp.model.Payment;
import carsharingapp.model.Rental;
import java.util.Set;

public interface NotificationService {

    void sendMessageAboutCreatedRental(Rental rental);

    void sendMessageAboutOverdueRental(Rental rental);

    void sendMessageAboutSuccessPayment(Payment payment, Car car);

    void sendMessageAboutCanceledPayment(Payment payment, Car car);

    void sendMessageAboutOverdueRentals(Set<Rental> overdueRentals);

}
