package com.example.agrilinkback.module.finance.entity;

import java.math.BigDecimal;

public record Bank(
        Integer bankId,
        String bankName,
        String introduce,
        String bankPhone,
        BigDecimal money,
        BigDecimal rate,
        String repayment
) {
}
