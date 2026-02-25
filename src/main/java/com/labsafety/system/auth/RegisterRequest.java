package com.labsafety.system.auth;

public record RegisterRequest(
        String username,
        String phone,
        String password,
        String role
) {
}