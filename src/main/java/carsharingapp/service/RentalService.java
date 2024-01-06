package carsharingapp.service;

import carsharingapp.dto.rental.CreateRentalRequestDto;
import carsharingapp.dto.rental.RentalDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RentalService {
    RentalDto createRental(CreateRentalRequestDto requestDto, Long userId);

    List<RentalDto> getAllRentals(Long userId, Pageable pageable);

    List<RentalDto> getAllActiveRentals(Long userId, Pageable pageable);

    List<RentalDto> getAllNonActiveRentals(Long userId, Pageable pageable);

    RentalDto setActualReturnDate(Long rentalId);
}
