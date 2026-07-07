package com.example.agrilinkback.common.security;

import java.util.Arrays;
import java.util.Optional;

public enum UserRole {
    BUYER("BUYER", "买家"),
    FARMER("FARMER", "农户"),
    EXPERT("EXPERT", "技术专家"),
    BANK("BANK", "银行"),
    SYSTEM_ADMIN("SYSTEM_ADMIN", "系统管理员");

    private final String code;
    private final String label;

    UserRole(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String code() {
        return code;
    }

    public String label() {
        return label;
    }

    public String authority() {
        return "ROLE_" + code;
    }

    public boolean isBusinessRole() {
        return this != SYSTEM_ADMIN;
    }

    public static Optional<UserRole> fromCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String normalized = code.trim().toUpperCase();
        return Arrays.stream(values())
                .filter(role -> role.code.equals(normalized))
                .findFirst();
    }
}
