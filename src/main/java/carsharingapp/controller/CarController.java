package carsharingapp.controller;

import carsharingapp.dto.car.CarRequestDto;
import carsharingapp.dto.car.CarResponseDto;
import carsharingapp.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Car management", description = "Endpoints for car management")
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = "Get all cars", description = "Get all cars from db")
    @GetMapping
    public List<CarResponseDto> getAll(Pageable pageable) {
        return carService.getAll(pageable);
    }

    @Operation(summary = "Add a new car", description = "Add a new car to db")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public CarResponseDto save(@RequestBody @Valid CarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @Operation(summary = "Get car by id", description = "Get car from db by id")
    @GetMapping("/{id}")
    public CarResponseDto get(@PathVariable Long id) {
        return carService.get(id);
    }

    @Operation(summary = "Update car", description = "Update already existing car")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public CarResponseDto update(@PathVariable Long id,
                                 @RequestBody @Valid CarRequestDto requestDto) {
        return carService.update(id, requestDto);
    }

    @Operation(summary = "Delete a car", description = "Delete a car by id")
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }
}
