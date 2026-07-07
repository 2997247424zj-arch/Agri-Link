package com.example.agrilinkback.module.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerRequest(
        @NotBlank String answer,
        @NotNull Integer status
) {
}
