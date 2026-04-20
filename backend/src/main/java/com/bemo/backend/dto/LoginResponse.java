package com.bemo.backend.dto;

import java.util.Set;

public class LoginResponse {

    private String token;
    private String username;
    private Set<String> roles;

    public LoginResponse(String token, String username, Set<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public Set<String> getRoles() { return roles; }
}