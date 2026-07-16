package com.example.agrilinkback.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailCodeRequest(
        @NotBlank
        @Pattern(regexp = "^[^\\s@]+@163\\.com$", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "must be a valid 163.com email address")
        String email
) {
}
