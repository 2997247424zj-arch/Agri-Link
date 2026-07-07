package com.example.agrilinkback.module.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String userName,
        @NotBlank String password,
        String nickName,
        String phone,
        String identityNum,
        String address,
        @NotBlank String role,
        String avatar,
        String realName
) {
}
