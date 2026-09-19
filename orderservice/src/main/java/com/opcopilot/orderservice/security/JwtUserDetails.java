package com.opcopilot.orderservice.security;

public class JwtUserDetails {
    private final String email;
    private final String name;

    public JwtUserDetails(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
