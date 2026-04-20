package com.bemo.backend.repository;

import com.bemo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    // existsByEmail eliminado: "email" no existe como columna en la tabla "usuario"
}