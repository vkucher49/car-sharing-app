package carsharingapp.service;

import carsharingapp.dto.rental.CreateRentalRequestDto;
import carsharingapp.dto.rental.RentalDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDto createRental(CreateRentalRequestDto requestDto, Long userId);

    List<RentalDto> getAllRentals(Long userId, Pageable pageable);

    List<RentalDto> getAllActiveRentals(Long userId, Pageable pageable);

    List<RentalDto> getAllNonActiveRentals(Long userId, Pageable pageable);

    RentalDto setActualReturnDate(Long rentalId);
}
