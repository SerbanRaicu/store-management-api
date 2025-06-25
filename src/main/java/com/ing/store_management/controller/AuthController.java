package com.ing.store_management.controller;

import com.ing.store_management.dto.CreateUserRequest;
import com.ing.store_management.dto.LoginRequest;
import com.ing.store_management.dto.LoginResponse;
import com.ing.store_management.dto.UserDto;
import com.ing.store_management.model.User;
import com.ing.store_management.repository.UserRepository;
import com.ing.store_management.security.JwtUtil;
import com.ing.store_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to register new user: {}", request.getUsername());

        try {
            UserDto userDto = UserDto.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .role(request.getRole() != null ? request.getRole() : User.Role.EMPLOYEE)
                    .enabled(true)
                    .build();

            userService.createUser(userDto, request.getPassword());

            log.info("User '{}' registered successfully", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User registered successfully",
                            "username", request.getUsername()
                    ));

        } catch (Exception e) {
            log.error("Registration failed for user '{}': {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login user: {}", request.getUsername());

        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

            if (!user.getEnabled()) {
                log.warn("Login attempt for disabled user: {}", request.getUsername());
                throw new BadCredentialsException("User account is disabled");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Invalid password attempt for user: {}", request.getUsername());
                throw new BadCredentialsException("Invalid username or password");
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .message("Login successful")
                    .build();

            log.info("User '{}' logged in successfully with role: {}", user.getUsername(), user.getRole());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.error("Login failed for user '{}': {}", request.getUsername(), e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .message("Invalid username or password")
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error during login for user '{}': {}", request.getUsername(), e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponse.builder()
                            .message("An error occurred during login")
                            .build());
        }
    }

}
