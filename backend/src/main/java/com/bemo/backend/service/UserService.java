package com.bemo.backend.service;

import com.bemo.backend.dto.RegisterRequest;
import com.bemo.backend.dto.UpdateUserRequest;
import com.bemo.backend.dto.UserDto;
import com.bemo.backend.model.Role;
import com.bemo.backend.model.User;
import com.bemo.backend.repository.RoleRepository;
import com.bemo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.Crypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!request.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("El nombre de usuario ya existe");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail().isBlank() ? null : request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
            user.getIsActive(), user.getCreatedAt(),
            user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }

    public UserDto updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        return new UserDto(
            user.getId(), user.getUsername(), user.getEmail(),
            user.getIsActive(), user.getCreatedAt(),
            user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(u -> new UserDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getIsActive(),
                u.getCreatedAt(),
                u.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
            ))
            .collect(Collectors.toList());
    }

    /**
     * Migración progresiva: si el hash almacenado es crypt (no bcrypt) y la contraseña
     * es correcta, re-hashea con bcrypt y guarda. Si el hash ya es bcrypt, no hace nada.
     */
    public void migratePasswordIfNeeded(String username, String plainPassword) {
        userRepository.findByUsername(username).ifPresent(user -> {
            String hash = user.getPassword();
            if (isBcrypt(hash)) return;
            try {
                if (Crypt.crypt(plainPassword, hash).equals(hash)) {
                    user.setPassword(passwordEncoder.encode(plainPassword));
                    userRepository.save(user);
                }
            } catch (Throwable ignored) {
                // Hash con formato desconocido o librería no disponible: dejamos que authManager falle normalmente
            }
        });
    }

    private boolean isBcrypt(String hash) {
        return hash != null &&
               (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        // Validación de email duplicado eliminada: "email" no es columna persistida en "usuario"

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
                roles.add(role);
            }
        } else {
            // Rol por defecto si no se especifica ninguno
            roleRepository.findByName("ROLE_PACIENTE").ifPresent(roles::add);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
}