package com.example.agrilinkback.module.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BankRequest(
        @NotBlank String bankName,
        String introduce,
        @NotBlank String bankPhone,
        @NotNull @DecimalMin("0.01") BigDecimal money,
        @NotNull @DecimalMin("0.00") BigDecimal rate,
        @NotBlank String repayment
) {
}
