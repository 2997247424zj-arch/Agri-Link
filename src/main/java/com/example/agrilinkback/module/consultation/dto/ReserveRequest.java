package com.example.agrilinkback.module.consultation.dto;

import jakarta.validation.constraints.NotBlank;

public record ReserveRequest(
        @NotBlank String expertName,
        @NotBlank String questioner,
        @NotBlank String area,
        @NotBlank String address,
        @NotBlank String plantName,
        @NotBlank String soilCondition,
        @NotBlank String plantCondition,
        @NotBlank String plantDetail,
        @NotBlank String phone,
        String message,
        String appointmentTime,
        String serviceMode
) {
}
