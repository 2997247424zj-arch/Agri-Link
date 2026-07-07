package com.example.agrilinkback.module.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ExpertRequest(
        @NotBlank String userName,
        @NotBlank String realName,
        @NotBlank String phone,
        String profession,
        String position,
        String belong
) {
}
