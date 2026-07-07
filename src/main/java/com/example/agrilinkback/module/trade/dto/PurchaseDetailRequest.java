package com.example.agrilinkback.module.trade.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PurchaseDetailRequest(
        @NotNull Integer orderId,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        @NotNull @Min(1) Integer count
) {
}
