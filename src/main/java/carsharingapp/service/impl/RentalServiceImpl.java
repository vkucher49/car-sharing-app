package carsharingapp.service.impl;

import carsharingapp.dto.rental.CreateRentalRequestDto;
import carsharingapp.dto.rental.RentalDto;
import carsharingapp.exception.EntityNotFoundException;
import carsharingapp.exception.NoAvailableCarsException;
import carsharingapp.exception.RentalIsExpiredException;
import carsharingapp.mapper.RentalMapper;
import carsharingapp.model.Car;
import carsharingapp.model.Rental;
import carsharingapp.repository.CarRepository;
import carsharingapp.repository.RentalRepository;
import carsharingapp.repository.UserRepository;
import carsharingapp.service.NotificationService;
import carsharingapp.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public RentalDto createRental(CreateRentalRequestDto requestDto, Long userId) {
        Car car = carRepository.findById(requestDto.carId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + requestDto.carId()));
        if (car.getInventory() < 1) {
            throw new NoAvailableCarsException("There no available cars at the moment");
        }
        car.setInventory(car.getInventory() - 1);
        Rental rental = new Rental();
        rental.setCar(car);
        rental.setUser(userRepository.getReferenceById(userId));
        rental.setRentalDateTime(LocalDateTime.now());
        rental.setReturnDateTime(requestDto.returnDateTime());
        notificationService.sendMessageAboutCreatedRental(rental);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalDto> getAllRentals(Long userId, Pageable pageable) {
        return rentalRepository.getAllByUserId(userId, pageable)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public List<RentalDto> getAllActiveRentals(Long userId, Pageable pageable) {
        return rentalRepository.getAllByUserIdAndActualReturnDateTimeIsNull(userId, pageable)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public List<RentalDto> getAllNonActiveRentals(Long userId, Pageable pageable) {
        return rentalRepository.getAllByUserIdAndActualReturnDateTimeNotNull(userId, pageable)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public RentalDto setActualReturnDate(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental by id: " + rentalId));
        if (rental.getActualReturnDateTime() != null) {
            throw new RentalIsExpiredException("This rental is already expired");
        }
        rental.setActualReturnDateTime(LocalDateTime.now());
        if (rental.getActualReturnDateTime().isAfter(rental.getReturnDateTime())) {
            notificationService.sendMessageAboutOverdueRental(rental);
        }
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        return rentalMapper.toDto(rental);
    }
}
