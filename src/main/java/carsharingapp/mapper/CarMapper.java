package carsharingapp.mapper;

import carsharingapp.config.MapperConfig;
import carsharingapp.dto.car.CarRequestDto;
import carsharingapp.dto.car.CarResponseDto;
import carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarResponseDto toDto(Car car);

    Car toModel(CarRequestDto requestDto);

    void updateCar(CarRequestDto requestDto, @MappingTarget Car car);
}
