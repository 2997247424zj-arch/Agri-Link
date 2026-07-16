package com.example.agrilinkback.module.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotBlank String ownName,
        @NotBlank String consignee,
        @NotBlank String phone,
        // 兼容调用方误传 address 字段名
        @NotBlank @JsonAlias({"address", "detail"}) String addressDetail,
        @NotNull Integer isDefault
) {
}
