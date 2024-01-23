package carsharingapp.dto.payment;

import carsharingapp.model.Payment;
import java.math.BigDecimal;
import java.net.URL;

public record PaymentDto(
        Long id,
        Payment.PaymentStatus status,
        Payment.PaymentType type,
        URL sessionUrl,
        String sessionId,
        BigDecimal price
) {
}
