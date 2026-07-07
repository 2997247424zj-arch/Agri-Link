package com.example.agrilinkback.module.knowledge.dto;

import jakarta.validation.constraints.NotBlank;

public record KnowledgeRequest(
        @NotBlank String title,
        @NotBlank String content,
        String picPath,
        @NotBlank String ownName
) {
}
