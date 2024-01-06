package carsharingapp.repository;

import carsharingapp.model.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByRentalUserId(Long id, Pageable pageable);

    List<Payment> findAllByStatus(Payment.PaymentStatus status);

    Optional<Payment> findBySessionId(String sessionId);

    List<Payment> findAllByRentalId(Long rentalId);
}
