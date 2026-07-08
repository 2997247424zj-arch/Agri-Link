package com.example.agrilinkback.module.admin.dto;

import jakarta.validation.constraints.NotNull;

/**
 * ??????/????????????
 */
public record AdminKnowledgeStatusRequest(
        @NotNull Integer status
) {
}
