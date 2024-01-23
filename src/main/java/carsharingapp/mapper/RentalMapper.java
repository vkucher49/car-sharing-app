package carsharingapp.mapper;

import carsharingapp.config.MapperConfig;
import carsharingapp.dto.rental.RentalDto;
import carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "car.brand", target = "carBrand")
    @Mapping(source = "car.model", target = "carModel")
    RentalDto toDto(Rental rental);
}
