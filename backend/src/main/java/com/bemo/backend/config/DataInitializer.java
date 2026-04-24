package com.bemo.backend.config;

import com.bemo.backend.model.Role;
import com.bemo.backend.model.User;
import com.bemo.backend.repository.RoleRepository;
import com.bemo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        insertRoles();
        insertAdminUser();
    }

    private void insertRoles() {
        crearRolSiNoExiste("ROLE_ADMIN",     "Administrador del sistema");
        crearRolSiNoExiste("ROLE_RECEPCION", "Operador de recepción");
        crearRolSiNoExiste("ROLE_DOCTOR",    "Médico / prestador");
        crearRolSiNoExiste("ROLE_PACIENTE",  "Paciente con acceso al portal");
        log.info("Roles cargados correctamente.");
    }

    private void crearRolSiNoExiste(String nombre, String descripcion) {
        if (roleRepository.findByName(nombre).isEmpty()) {
            Role rol = new Role();
            rol.setName(nombre);
            rol.setDescription(descripcion);
            roleRepository.save(rol);
            log.info("  Rol creado: {}", nombre);
        }
    }

    private void insertAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }
        Role rolAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN no encontrado"));

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@ecomed.com");
        admin.setNombre("Administrador");
        admin.setNivel(1);
        admin.setIsActive(true);
        admin.setRoles(Set.of(rolAdmin));

        userRepository.save(admin);
        log.info("Usuario admin creado (user: admin / pass: admin123). Cambiá la contraseña en producción.");
    }
}
