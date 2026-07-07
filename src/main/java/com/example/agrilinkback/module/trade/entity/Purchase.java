package com.example.agrilinkback.module.trade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Purchase(
        Integer purchaseId,
        String ownName,
        Integer purchaseType,
        BigDecimal totalPrice,
        String address,
        Integer purchaseStatus,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
