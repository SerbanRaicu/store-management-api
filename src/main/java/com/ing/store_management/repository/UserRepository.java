package com.ing.store_management.repository;

import com.ing.store_management.model.Product;
import com.ing.store_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(User.Role role);

    List<User> findByEnabledTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
