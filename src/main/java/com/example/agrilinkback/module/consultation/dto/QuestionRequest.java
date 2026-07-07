package com.example.agrilinkback.module.consultation.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionRequest(
        @NotBlank String expertName,
        @NotBlank String questioner,
        @NotBlank String phone,
        @NotBlank String plantName,
        @NotBlank String title,
        String question
) {
}
