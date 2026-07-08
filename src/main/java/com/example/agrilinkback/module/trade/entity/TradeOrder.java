package com.example.agrilinkback.module.trade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeOrder(
        Integer orderId,
        String title,
        BigDecimal price,
        String content,
        Integer orderStatus,
        String type,
        String picture,
        String ownName,
        String cooperationName,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        String address,
        Integer stock,
        String spec,
        String unit,
        Integer minPurchase
) {
}
