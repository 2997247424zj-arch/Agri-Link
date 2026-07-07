package com.example.agrilinkback.module.trade.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PurchaseRequest(
        @NotBlank String ownName,
        @NotNull Integer purchaseType,
        @NotBlank String address,
        @NotEmpty List<@Valid PurchaseDetailRequest> details
) {
}
