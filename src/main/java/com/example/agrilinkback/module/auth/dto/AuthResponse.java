package com.example.agrilinkback.module.auth.dto;

public record AuthResponse(
        String userName,
        String nickName,
        String realName,
        String role,
        String roleLabel,
        String token,
        String headerName
) {
}
