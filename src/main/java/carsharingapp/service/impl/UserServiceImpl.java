package carsharingapp.service.impl;

import carsharingapp.dto.user.UserRegistrationRequestDto;
import carsharingapp.dto.user.UserResponseDto;
import carsharingapp.exception.EntityNotFoundException;
import carsharingapp.exception.RegistrationException;
import carsharingapp.mapper.UserMapper;
import carsharingapp.model.Role;
import carsharingapp.model.User;
import carsharingapp.repository.RoleRepository;
import carsharingapp.repository.UserRepository;
import carsharingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with this email already exists!");
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        Role userRole = roleRepository.findRoleByName(Role.RoleName.CUSTOMER).orElseThrow(
                () -> new RegistrationException("Can't find role by name"));
        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(userRole);
        user.setRoles(defaultRoles);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUserRole(Long id, String roleDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find role by name"));
        Role roleByName = roleRepository.findRoleByName(Role.RoleName.valueOf(roleDto))
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find role by name: " + roleDto));
        user.getRoles().add(roleByName);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateInfo(Authentication authentication,
                                      UserRegistrationRequestDto requestDto) {
        final User authenticationUser = (User) authentication.getPrincipal();
        authenticationUser.setEmail(requestDto.getEmail());
        authenticationUser.setPassword(requestDto.getPassword());
        authenticationUser.setFirstName(requestDto.getFirstName());
        authenticationUser.setLastName(requestDto.getLastName());
        return userMapper.toDto(userRepository.save(authenticationUser));
    }
}
