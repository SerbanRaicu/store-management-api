package com.ing.store_management.model;

import com.ing.store_management.util.AbstractTimestampEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@ToString(exclude = {"password"})
public class User extends AbstractTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Column(name = "first_name", length = 50)
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Column(name = "last_name", length = 50)
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Role is required")
    private Role role;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    public enum Role {
        ADMIN, MANAGER, EMPLOYEE
    }
}
