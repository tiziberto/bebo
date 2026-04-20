package com.bemo.backend.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Set<String> roles;

    public UserDto(Long id, String username, String email, Boolean isActive, LocalDateTime createdAt, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Boolean getIsActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<String> getRoles() { return roles; }
}
