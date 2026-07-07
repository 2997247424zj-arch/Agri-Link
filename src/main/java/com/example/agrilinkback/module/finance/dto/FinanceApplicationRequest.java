package com.example.agrilinkback.module.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FinanceApplicationRequest(
        @NotNull Integer bankId,
        @NotBlank String ownName,
        @NotBlank String realName,
        @NotBlank String phone,
        @NotBlank String idNum,
        @NotNull @DecimalMin("0.01") BigDecimal money,
        @NotNull @DecimalMin("0.00") BigDecimal rate,
        @NotBlank String repayment,
        String combinationName1,
        String combinationPhone1,
        String combinationIdnum1,
        String combinationName2,
        String combinationPhone2,
        String combinationIdnum2,
        String fileInfo
) {
}
