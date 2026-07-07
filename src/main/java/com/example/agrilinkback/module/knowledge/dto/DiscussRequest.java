package com.example.agrilinkback.module.knowledge.dto;

import jakarta.validation.constraints.NotBlank;

public record DiscussRequest(
        @NotBlank String ownName,
        @NotBlank String content
) {
}
