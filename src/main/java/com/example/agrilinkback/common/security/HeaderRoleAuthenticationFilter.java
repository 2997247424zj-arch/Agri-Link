package com.example.agrilinkback.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 基于请求头的轻量角色认证过滤器。
 *
 * <p>当前实训项目使用 X-User-Role 表示登录后的角色，便于前后端联调和 MockMvc 测试。
 */
@Component
public class HeaderRoleAuthenticationFilter extends OncePerRequestFilter {

    public static final String ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        UserRole.fromCode(request.getHeader(ROLE_HEADER)).ifPresent(role -> {
            // Spring Security 的 hasRole 会匹配 ROLE_ 前缀权限，这里统一转换一次。
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    role.code(),
                    null,
                    List.of(new SimpleGrantedAuthority(role.authority()))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 过滤器复用线程时必须清理上下文，避免角色信息串到下一次请求。
            SecurityContextHolder.clearContext();
        }
    }
}
