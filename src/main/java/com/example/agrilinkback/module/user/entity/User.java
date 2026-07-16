package com.example.agrilinkback.module.user.entity;

import java.time.LocalDateTime;

public record User(
        String userName,
        String password,
        String nickName,
        String phone,
        String identityNum,
        String address,
        String role,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        Integer integral,
        Integer credit,
        String avatar,
        String realName,
        Boolean enabled
) {
}
