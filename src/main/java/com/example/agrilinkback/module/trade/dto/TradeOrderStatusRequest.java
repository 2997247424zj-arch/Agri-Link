package com.example.agrilinkback.module.trade.dto;

import jakarta.validation.constraints.NotNull;

public record TradeOrderStatusRequest(
        @NotNull Integer orderStatus,
        String cooperationName
) {
}
