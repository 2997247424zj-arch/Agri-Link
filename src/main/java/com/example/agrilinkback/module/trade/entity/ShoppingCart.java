package com.example.agrilinkback.module.trade.entity;

import java.time.LocalDateTime;

public record ShoppingCart(
        Integer shoppingId,
        Integer orderId,
        Integer count,
        String ownName,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
