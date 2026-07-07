package com.example.agrilinkback.module.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FinancingIntentionRequest(
        @NotBlank String userName,
        @NotBlank String realName,
        @NotBlank String address,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String application,
        String item,
        String repaymentPeriod,
        String area,
        String phone
) {
}
