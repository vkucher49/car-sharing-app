package carsharingapp.service.impl;

import carsharingapp.dto.car.CarRequestDto;
import carsharingapp.dto.car.CarResponseDto;
import carsharingapp.mapper.CarMapper;
import carsharingapp.model.Car;
import carsharingapp.repository.CarRepository;
import carsharingapp.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarResponseDto> getAll(Pageable pageable) {
        Page<Car> cars = carRepository.findAll(pageable);
        return cars.getContent().stream()
                .map(carMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CarResponseDto save(CarRequestDto requestDto) {
        final Car car = carMapper.toModel(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public CarResponseDto get(Long id) {
        return carMapper.toDto(carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id)));
    }

    @Override
    public CarResponseDto update(Long id, CarRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id));
        carMapper.updateCar(requestDto, car);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void delete(Long id) {
        carRepository.deleteById(id);
    }
}
