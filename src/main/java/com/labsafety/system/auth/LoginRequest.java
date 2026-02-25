package com.labsafety.system.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String identifier, // username OR phone
        @NotBlank String password
) {
}