package carsharingapp.service;

import carsharingapp.dto.car.CarRequestDto;
import carsharingapp.dto.car.CarResponseDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CarService {
    List<CarResponseDto> getAll(Pageable pageable);

    CarResponseDto save(CarRequestDto requestDto);

    CarResponseDto get(Long id);

    CarResponseDto update(Long id, CarRequestDto requestDto);

    void delete(Long id);
}
