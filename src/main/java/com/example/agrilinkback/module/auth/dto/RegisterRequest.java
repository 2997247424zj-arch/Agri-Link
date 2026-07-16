package com.example.agrilinkback.module.auth.dto;

import com.example.agrilinkback.module.user.dto.UserRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank String userName,
        @NotBlank String password,
        @Pattern(regexp = "^\\d{6}$", message = "must contain 6 digits") String verificationCode,
        String nickName,
        String phone,
        String identityNum,
        String address,
        @NotBlank String role,
        String avatar,
        String realName
) {
    public UserRequest toUserRequest(String normalizedUserName) {
        return new UserRequest(normalizedUserName, password, nickName, phone, identityNum,
                address, role, avatar, realName);
    }
}
