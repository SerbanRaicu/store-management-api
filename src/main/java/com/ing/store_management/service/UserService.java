package com.ing.store_management.service;

import com.ing.store_management.dto.UserDto;
import com.ing.store_management.exception.DuplicateUserException;
import com.ing.store_management.exception.UserNotFoundException;
import com.ing.store_management.model.User;
import com.ing.store_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(UserDto userDto, String rawPassword) {
        log.info("Creating new user: {}", userDto.getUsername());

        if (userRepository.existsByUsername(userDto.getUsername())) {
            log.error("Username '{}' already exists", userDto.getUsername());
            throw new DuplicateUserException("Username '" + userDto.getUsername() + "' already exists");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("Email '{}' already exists", userDto.getEmail());
            throw new DuplicateUserException("Email '" + userDto.getEmail() + "' already exists");
        }

        User user = mapToEntity(userDto);
        user.setPassword(passwordEncoder.encode(rawPassword));
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return mapToDto(savedUser);
    }

    public UserDto findUserById(Long id) {
        log.info("Finding user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });

        return mapToDto(user);
    }

    public UserDto findUserByUsername(String username) {
        log.info("Finding user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });

        return mapToDto(user);
    }

    public UserDto findUserByEmail(String email) {
        log.info("Finding user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with username: " + email);
                });

        return mapToDto(user);
    }

    public List<UserDto> findUsersByRole(User.Role role) {
        log.info("Finding users by role: {}", role);

        List<User> users = userRepository.findByRole(role);
        log.info("Found {} users with role: {}", users.size(), role);

        return users.stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<UserDto> findAllActiveUsers() {
        log.info("Finding all active users");

        List<User> users = userRepository.findByEnabledTrue();
        log.info("Found {} active users", users.size());

        return users.stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserDto updateUserRole(Long userId, User.Role newRole) {
        log.info("Updating role for user ID: {} to {}", userId, newRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        User.Role oldRole = user.getRole();
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        log.info("Role updated successfully for user '{}' from {} to {}",
                user.getUsername(), oldRole, newRole);

        return mapToDto(updatedUser);
    }

    public UserDto enableUser(Long userId) {
        log.info("Enabling user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        user.setEnabled(true);
        User updatedUser = userRepository.save(user);

        log.info("User '{}' enabled successfully", user.getUsername());
        return mapToDto(updatedUser);
    }

    public UserDto disableUser(Long userId) {
        log.info("Disabling user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        user.setEnabled(false);
        User updatedUser = userRepository.save(user);

        log.info("User '{}' disabled successfully", user.getUsername());
        return mapToDto(updatedUser);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User mapToEntity(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRole(userDto.getRole());
        user.setEnabled(userDto.getEnabled() != null ? userDto.getEnabled() : true);
        return user;
    }
}
