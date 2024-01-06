package carsharingapp.controller;

import carsharingapp.dto.rental.CreateRentalRequestDto;
import carsharingapp.dto.rental.RentalDto;
import carsharingapp.model.User;
import carsharingapp.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Rentals management", description = "Endpoints for rentals management")
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Create a new rental", description = "Creating a new rental")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public RentalDto createRental(@RequestBody CreateRentalRequestDto requestDto,
                                  Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.createRental(requestDto, user.getId());
    }

    @Operation(summary = "Get all rentals", description = "Get all rentals")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public List<RentalDto> getAll(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                  Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.getAllRentals(user.getId(), pageable);
    }

    @Operation(summary = "Get all active rentals",
            description = "Get all active rentals at the moment")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    public List<RentalDto> getAllRentalsByActivity(@RequestParam(name = "is_active") boolean isActive,
                                                   Authentication authentication,
                                                   @PageableDefault(page = 0, size = 10)
                                                   Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        if (isActive) {
            return rentalService.getAllActiveRentals(user.getId(), pageable);
        } else {
            return rentalService.getAllNonActiveRentals(user.getId(), pageable);
        }
    }

    @Operation(summary = "Search rentals",
            description = "Searching rentals using userId and activities")
    @GetMapping("/search/")
    @PreAuthorize("hasRole('MANAGER')")
    public List<RentalDto> getAllRentalsByUserAndActivities(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") boolean isActive,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (isActive) {
            return rentalService.getAllActiveRentals(userId, pageable);
        } else {
            return rentalService.getAllNonActiveRentals(userId, pageable);
        }
    }

    @Operation(summary = "Return rental by id", description = "Returning rental")
    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('MANAGER')")
    public RentalDto returnRental(@PathVariable @Positive Long id) {
        return rentalService.setActualReturnDate(id);
    }
}
