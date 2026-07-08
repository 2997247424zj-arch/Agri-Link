package com.example.agrilinkback.module.trade.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TradeOrderRequest(
        @NotBlank String title,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotBlank String content,
        @NotBlank String type,
        String picture,
        @NotBlank String ownName,
        String address,
        Integer stock,
        String spec,
        String unit,
        Integer minPurchase
) {
}
