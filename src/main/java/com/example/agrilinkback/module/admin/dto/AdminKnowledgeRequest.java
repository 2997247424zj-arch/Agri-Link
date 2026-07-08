package com.example.agrilinkback.module.admin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ????????????????
 */
public record AdminKnowledgeRequest(
        @NotBlank String title,
        String category,
        @NotBlank String summary,
        Integer status
) {
}
