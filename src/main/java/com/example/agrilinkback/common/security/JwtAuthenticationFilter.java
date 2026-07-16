package com.example.agrilinkback.common.security;

import com.example.agrilinkback.module.user.entity.User;
import com.example.agrilinkback.module.user.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 基于 Bearer JWT 的无状态认证过滤器。
 *
 * <p>从 {@code Authorization: Bearer <token>} 提取并校验 JWT，
 * 校验通过后把用户名与角色写入 SecurityContext。被禁用账号不会获得认证。</p>
 *
 * <p>不再信任客户端可随意伪造的 {@code X-User-Role}/{@code X-User-Name} 请求头。</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = HttpHeaders.AUTHORIZATION;

    private final JwtService jwtService;
    private final UserMapper userMapper;

    public JwtAuthenticationFilter(JwtService jwtService, UserMapper userMapper) {
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractBearerToken(request.getHeader(TOKEN_HEADER));
        if (token != null) {
            jwtService.parseClaims(token).ifPresent(this::authenticate);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void authenticate(Claims claims) {
        String userName = claims.getSubject();
        if (userName == null || userName.isBlank()) {
            return;
        }

        String roleCode = claims.get(JwtService.CLAIM_ROLE, String.class);
        UserRole role = UserRole.fromCode(roleCode).orElse(null);
        if (role == null) {
            return;
        }

        User user = userMapper.findByUserName(userName);
        if (user == null || Boolean.FALSE.equals(user.enabled())) {
            return;
        }

        // 以数据库当前角色为准，防止令牌中的角色与账号实际角色漂移。
        UserRole effectiveRole = UserRole.fromCode(user.role()).orElse(role);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userName,
                null,
                List.of(new SimpleGrantedAuthority(effectiveRole.authority()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static String extractBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        String value = authorization.trim();
        if (value.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            String token = value.substring(BEARER_PREFIX.length()).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }
}
