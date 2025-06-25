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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private CreateUserRequest createUserRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .role(User.Role.EMPLOYEE)
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.EMPLOYEE);
        user.setEnabled(true);

        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(User.Role.EMPLOYEE)
                .enabled(true)
                .build();
    }

    @Test
    void register_Success() {
        when(userService.createUser(any(UserDto.class), anyString())).thenReturn(userDto);

        UserDto result = authService.register(createUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getRole()).isEqualTo(User.Role.EMPLOYEE);
        verify(userService).createUser(any(UserDto.class), eq("password123"));
    }

    @Test
    void register_WithNullRole_DefaultsToEmployee() {
        createUserRequest.setRole(null);
        when(userService.createUser(any(UserDto.class), anyString())).thenReturn(userDto);

        UserDto result = authService.register(createUserRequest);

        assertThat(result.getRole()).isEqualTo(User.Role.EMPLOYEE);
        verify(userService).createUser(argThat(dto -> dto.getRole() == User.Role.EMPLOYEE), anyString());
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "EMPLOYEE")).thenReturn("jwt-token");

        LoginResponse result = authService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getRole()).isEqualTo(User.Role.EMPLOYEE);
        assertThat(result.getMessage()).isEqualTo("Login successful");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_AccountDisabled_ThrowsException() {
        user.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AccountDisabledException.class)
                .hasMessage("User account 'testuser' is disabled");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}
