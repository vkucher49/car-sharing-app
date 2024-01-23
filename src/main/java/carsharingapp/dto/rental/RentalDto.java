package carsharingapp.dto.rental;

import java.time.LocalDateTime;

public record RentalDto(
        Long id,
        Long carId,
        String carBrand,
        String carModel,
        LocalDateTime rentalDateTime,
        LocalDateTime returnDateTime,
        LocalDateTime actualReturnDateTime
) {
}
