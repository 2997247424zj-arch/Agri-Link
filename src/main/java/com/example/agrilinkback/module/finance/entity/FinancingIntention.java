package com.example.agrilinkback.module.finance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinancingIntention(
        Integer id,
        String userName,
        String realName,
        String address,
        BigDecimal amount,
        String application,
        String item,
        String repaymentPeriod,
        String area,
        String phone,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
