package com.ing.store_management.service;

import com.ing.store_management.dto.CreateUserRequest;
import com.ing.store_management.dto.LoginRequest;
import com.ing.store_management.dto.LoginResponse;
import com.ing.store_management.dto.UserDto;
import com.ing.store_management.exception.AccountDisabledException;
import com.ing.store_management.exception.InvalidCredentialsException;
import com.ing.store_management.model.User;
import com.ing.store_management.repository.UserRepository;
import com.ing.store_management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserDto register(CreateUserRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        UserDto userDto = UserDto.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole() != null ? request.getRole() : User.Role.EMPLOYEE)
                .enabled(true)
                .build();

        UserDto createdUser = userService.createUser(userDto, request.getPassword());
        log.info("User '{}' registered successfully with role: {}", createdUser.getUsername(), createdUser.getRole());

        return createdUser;
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Processing login request for user: {}", request.getUsername());

        // Find user or throw exception
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login attempt for non-existent user: {}", request.getUsername());
                    return new InvalidCredentialsException();
                });

        if (!user.getEnabled()) {
            log.warn("Login attempt for disabled user: {}", request.getUsername());
            throw new AccountDisabledException(request.getUsername());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", request.getUsername());
            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .message("Login successful")
                .build();

        log.info("User '{}' logged in successfully with role: {}", user.getUsername(), user.getRole());

        return response;
    }
}
