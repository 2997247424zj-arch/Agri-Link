package com.example.agrilinkback.common.security;

import com.example.agrilinkback.module.user.entity.User;
import com.example.agrilinkback.module.user.mapper.UserMapper;
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
 * <p>从 {@code X-User-Role} 提取角色、{@code X-User-Name} 提取用户名，
 * 校验账号启用状态后注入 Spring Security 上下文。被禁用的用户不会获得认证，
 * 请求将被 Spring Security 以 401 拒绝，实现"即时生效"的账号禁用。</p>
 */
@Component
public class HeaderRoleAuthenticationFilter extends OncePerRequestFilter {

    public static final String ROLE_HEADER = "X-User-Role";
    public static final String USER_HEADER = "X-User-Name";

    private final UserMapper userMapper;

    public HeaderRoleAuthenticationFilter(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String roleCode = request.getHeader(ROLE_HEADER);
        String userName = request.getHeader(USER_HEADER);

        UserRole.fromCode(roleCode).ifPresent(role -> {
            // 账号禁用检查：管理员禁用用户后即时生效
            if (userName != null && !userName.isBlank()) {
                User user = userMapper.findByUserName(userName);
                if (user != null && Boolean.FALSE.equals(user.enabled())) {
                    return;
                }
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userName != null && !userName.isBlank() ? userName : role.code(),
                    null,
                    List.of(new SimpleGrantedAuthority(role.authority()))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
