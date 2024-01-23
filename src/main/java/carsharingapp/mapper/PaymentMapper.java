package carsharingapp.mapper;

import carsharingapp.config.MapperConfig;
import carsharingapp.dto.payment.PaymentDto;
import carsharingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);
}
