package carsharingapp.controller;

import carsharingapp.dto.user.UserRegistrationRequestDto;
import carsharingapp.dto.user.UserResponseDto;
import carsharingapp.dto.user.UserUpdateRoleRequestDto;
import carsharingapp.mapper.UserMapper;
import carsharingapp.model.User;
import carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Users management", description = "Endpoints for users management")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Update user's role", description = "Update user's role")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @RequestBody UserUpdateRoleRequestDto requestDto) {
        return userService.updateUserRole(id, requestDto.role());
    }

    @Operation(summary = "Get user's personal information",
            description = "Get user's firstname, lastname and email")
    @GetMapping("/me")
    public UserResponseDto getInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userMapper.toDto(user);
    }

    @Operation(summary = "Update user's information",
            description = "Update user's firstname, lastname or email")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/update")
    public UserResponseDto updateInfo(@RequestBody @Valid UserRegistrationRequestDto requestDto,
                                      Authentication authentication) {
        return userService.updateInfo(authentication, requestDto);
    }
}
