package com.example.agrilinkback.module.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
        @NotBlank String password
) {
}
