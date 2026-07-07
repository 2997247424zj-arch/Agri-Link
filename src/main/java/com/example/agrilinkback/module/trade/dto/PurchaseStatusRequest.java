package com.example.agrilinkback.module.trade.dto;

import jakarta.validation.constraints.NotNull;

public record PurchaseStatusRequest(
        @NotNull Integer purchaseStatus
) {
}
