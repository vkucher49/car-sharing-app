package carsharingapp.service;

import carsharingapp.dto.user.UserRegistrationRequestDto;
import carsharingapp.dto.user.UserResponseDto;
import carsharingapp.exception.RegistrationException;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto updateUserRole(Long id, String roleDto);

    UserResponseDto updateInfo(Authentication authentication,
                               UserRegistrationRequestDto requestDto);
}
