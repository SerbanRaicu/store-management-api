package com.ing.store_management.controller;

import com.ing.store_management.dto.UserDto;
import com.ing.store_management.model.User;
import com.ing.store_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.info("REST request to get user by ID: {}", id);
        UserDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        log.info("REST request to get user by username: {}", username);
        UserDto user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable User.Role role) {
        log.info("REST request to get users by role: {}", role);
        List<UserDto> users = userService.findUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserDto>> getActiveUsers() {
        log.info("REST request to get all active users");
        List<UserDto> users = userService.findAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("REST request to get all users");
        // Get all active users for now
        List<UserDto> users = userService.findAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, User.Role> request) {
        log.info("REST request to update role for user ID: {}", id);

        User.Role newRole = request.get("role");
        if (newRole == null) {
            throw new IllegalArgumentException("Role is required");
        }

        UserDto updatedUser = userService.updateUserRole(id, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<UserDto> enableUser(@PathVariable Long id) {
        log.info("REST request to enable user with ID: {}", id);
        UserDto updatedUser = userService.enableUser(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<UserDto> disableUser(@PathVariable Long id) {
        log.info("REST request to disable user with ID: {}", id);
        UserDto updatedUser = userService.disableUser(id);
        return ResponseEntity.ok(updatedUser);
    }
}
