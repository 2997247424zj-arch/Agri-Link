package com.example.agrilinkback.module.consultation.dto;

import jakarta.validation.constraints.NotNull;

/**
 * ????????????status ?? tb_reserve.status ?????
 */
public record ReserveStatusRequest(
        @NotNull Integer status
) {
}
