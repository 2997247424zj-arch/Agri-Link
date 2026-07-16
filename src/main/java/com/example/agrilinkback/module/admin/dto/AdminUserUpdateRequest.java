package com.example.agrilinkback.module.admin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理员修改用户身份信息请求体。
 */
public record AdminUserUpdateRequest(
        @NotBlank String nickName,
        String phone,
        String identityNum,
        String realName,
        String address
) {
}
