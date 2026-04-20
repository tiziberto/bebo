package com.bemo.backend.security;

import com.bemo.backend.model.User;
import com.bemo.backend.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(roleFromNivel(user.getNivel()))))
                .build();
    }

    private String roleFromNivel(Integer nivel) {
        if (nivel == null) return "ROLE_USER";
        return switch (nivel) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_DOCTOR";
            case 3 -> "ROLE_RECEPCION";
            default -> "ROLE_USER";
        };
    }
}