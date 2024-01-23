package carsharingapp.service;

import carsharingapp.dto.car.CarRequestDto;
import carsharingapp.dto.car.CarResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    List<CarResponseDto> getAll(Pageable pageable);

    CarResponseDto save(CarRequestDto requestDto);

    CarResponseDto get(Long id);

    CarResponseDto update(Long id, CarRequestDto requestDto);

    void delete(Long id);
}
