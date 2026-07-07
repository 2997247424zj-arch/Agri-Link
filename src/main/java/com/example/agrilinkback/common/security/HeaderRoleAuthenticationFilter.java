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
            SecurityContextHolder.clearContext();
        }
    }
}
