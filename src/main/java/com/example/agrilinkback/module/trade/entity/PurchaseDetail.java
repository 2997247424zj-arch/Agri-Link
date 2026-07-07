package com.example.agrilinkback.module.trade.entity;

import java.math.BigDecimal;

public record PurchaseDetail(
        Integer detailId,
        Integer purchaseId,
        Integer orderId,
        BigDecimal uninPrice,
        BigDecimal sumPrice,
        Integer count
) {
}
