package com.example.agrilinkback.module.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminRoleRequest(
        @NotBlank String role
) {
}
