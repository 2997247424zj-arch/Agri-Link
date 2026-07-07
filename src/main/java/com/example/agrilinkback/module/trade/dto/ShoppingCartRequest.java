package com.example.agrilinkback.module.trade.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShoppingCartRequest(
        @NotNull Integer orderId,
        @NotNull @Min(1) Integer count,
        @NotBlank String ownName
) {
}
