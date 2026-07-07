package com.example.agrilinkback.module.finance.dto;

import jakarta.validation.constraints.NotNull;

public record FinanceStatusRequest(
        @NotNull Integer status,
        String remark
) {
}
