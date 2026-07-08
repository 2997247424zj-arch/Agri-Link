package com.example.agrilinkback.module.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record QuestionRequest(
        @NotBlank String expertName,
        @NotBlank String questioner,
        @NotBlank String phone,
        @NotBlank String plantName,
        @NotBlank String title,
        String question,
        List<String> attachments
) {
}
