package com.example.agrilinkback.config;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.common.security.HeaderRoleAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 后端接口权限矩阵配置。
 *
 * <p>公共查询接口放行，写操作和个人/后台接口按 BUYER、FARMER、EXPERT、BANK、SYSTEM_ADMIN 控制。
 */
@Configuration
public class SecurityConfig {

    private final HeaderRoleAuthenticationFilter headerRoleAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(HeaderRoleAuthenticationFilter headerRoleAuthenticationFilter, ObjectMapper objectMapper) {
        this.headerRoleAuthenticationFilter = headerRoleAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离接口不依赖 Cookie Session，关闭 CSRF 并使用无状态会话。
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, ex) ->
                                writeError(response, HttpStatus.UNAUTHORIZED, "Authentication required"))
                        .accessDeniedHandler((request, response, ex) ->
                                writeError(response, HttpStatus.FORBIDDEN, "Access denied")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 健康检查、角色字典、静态文件和接口文档属于公共资源。
                        .requestMatchers(
                                "/api/system/health",
                                "/api/system/roles",
                                "/files/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("SYSTEM_ADMIN")
                        // 图片上传需要已登录角色，避免匿名上传文件。
                        .requestMatchers(HttpMethod.POST, "/api/files/images")
                                .hasAnyRole("BUYER", "FARMER", "EXPERT", "BANK", "SYSTEM_ADMIN")
                        // 融资匹配接口有方向性：农户匹配银行，银行匹配农户。
                        .requestMatchers(HttpMethod.GET, "/api/finance/banks/matches").hasRole("FARMER")
                        .requestMatchers(HttpMethod.GET, "/api/finance/matches/farmers/**").hasRole("BANK")
                        // 展示型查询对游客开放，写操作在后续规则中继续限制。
                        .requestMatchers(HttpMethod.GET,
                                "/api/finance/banks/**",
                                "/api/knowledge/**",
                                "/api/experts/**",
                                "/api/trade/orders/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/knowledge").hasRole("EXPERT")
                        .requestMatchers(HttpMethod.PUT, "/api/knowledge/*").hasRole("EXPERT")
                        .requestMatchers(HttpMethod.DELETE, "/api/knowledge/*").hasRole("EXPERT")
                        .requestMatchers("/api/experts/**").hasRole("EXPERT")
                        .requestMatchers("/api/users/**").hasAnyRole("BUYER", "FARMER", "EXPERT", "BANK")
                        .requestMatchers("/api/addresses/**").hasAnyRole("BUYER", "FARMER")
                        .requestMatchers("/api/trade/orders/**").hasAnyRole("BUYER", "FARMER")
                        .requestMatchers("/api/trade/shopping-cart/**").hasRole("BUYER")
                        .requestMatchers("/api/trade/purchases/**").hasAnyRole("BUYER", "FARMER")
                        .requestMatchers("/api/finance/banks/**").hasRole("BANK")
                        .requestMatchers("/api/knowledge/**").hasAnyRole("FARMER", "EXPERT")
                        .requestMatchers("/api/consultation/**").hasAnyRole("FARMER", "EXPERT")
                        .requestMatchers("/api/finance/applications/**").hasAnyRole("FARMER", "BANK")
                        .requestMatchers("/api/finance/intentions/**").hasAnyRole("FARMER", "BANK")
                        .anyRequest().authenticated())
                .addFilterBefore(headerRoleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws java.io.IOException {
        // Security 异常不经过 ControllerAdvice，这里手动写出统一响应结构。
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(status.value(), message));
    }
}
