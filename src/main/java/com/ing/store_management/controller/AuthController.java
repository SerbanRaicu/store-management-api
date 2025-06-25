package com.ing.store_management.controller;

import com.ing.store_management.dto.CreateUserRequest;
import com.ing.store_management.dto.LoginRequest;
import com.ing.store_management.dto.LoginResponse;
import com.ing.store_management.dto.UserDto;
import com.ing.store_management.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("REST request to register new user: {}", request.getUsername());

        UserDto createdUser = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "username", createdUser.getUsername(),
                        "role", createdUser.getRole().name()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login user: {}", request.getUsername());

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}

