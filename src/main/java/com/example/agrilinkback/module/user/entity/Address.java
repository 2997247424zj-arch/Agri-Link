package com.example.agrilinkback.module.user.entity;

public record Address(
        Integer id,
        String ownName,
        String consignee,
        String phone,
        String addressDetail,
        Integer isDefault
) {
}
