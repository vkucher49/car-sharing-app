package carsharingapp.dto.car;

import carsharingapp.model.Car;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarRequestDto {
    private String model;
    private String brand;
    private Car.CarType carType;
    private Integer inventory;
    private BigDecimal dailyFee;
}
