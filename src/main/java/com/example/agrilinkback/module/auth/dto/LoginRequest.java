package com.example.agrilinkback.module.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String userName,
        @NotBlank String password,
        String role
) {
}
