package com.example.agrilinkback.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotBlank String ownName,
        @NotBlank String consignee,
        @NotBlank String phone,
        @NotBlank String addressDetail,
        @NotNull Integer isDefault
) {
}
