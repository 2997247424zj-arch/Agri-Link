package com.example.agrilinkback.module.user.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Address(
        Integer id,
        String ownName,
        String consignee,
        String phone,
        // 主字段 addressDetail；同时兼容读取方使用 address
        @JsonProperty("addressDetail")
        @JsonAlias({"address", "detail"})
        String addressDetail,
        Integer isDefault
) {
        /** 兼容旧客户端读取 address 字段。 */
        @JsonProperty("address")
        public String address() {
                return addressDetail;
        }
}
