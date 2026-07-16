package com.example.agrilinkback.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 从 Spring Security 上下文读取当前登录用户。
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * @return 当前认证主体用户名；未登录时返回空串
     */
    public static String currentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "";
        }
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return "";
        }
        String name = String.valueOf(principal);
        if ("anonymousUser".equalsIgnoreCase(name)) {
            return "";
        }
        return name;
    }
}
