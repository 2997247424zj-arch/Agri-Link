package com.example.agrilinkback.common.security;

/**
 * @deprecated 已由 {@link JwtAuthenticationFilter} 替代。保留类名常量仅供兼容迁移说明。
 * 旧实现信任客户端可伪造的 {@code X-User-Role}/{@code X-User-Name}，存在严重越权风险。
 */
@Deprecated(forRemoval = true)
public final class HeaderRoleAuthenticationFilter {

    public static final String ROLE_HEADER = "X-User-Role";
    public static final String USER_HEADER = "X-User-Name";

    private HeaderRoleAuthenticationFilter() {
    }
}
