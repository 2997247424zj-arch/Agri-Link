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
 * Spring Security 核心配置类，负责定义整个平台的 HTTP 接口权限矩阵与安全策略。
 *
 * <h2>设计目标</h2>
 * <p>本平台采用前后端分离架构，后端仅提供 RESTful API，不维护传统 Servlet 会话。
 * 因此安全配置围绕以下原则展开：</p>
 * <ul>
 *   <li><strong>无状态认证</strong> —— 关闭 CSRF、禁用 Session，认证信息通过请求头（Header）
 *       由 {@link HeaderRoleAuthenticationFilter} 提取并注入安全上下文。</li>
 *   <li><strong>角色驱动授权</strong> —— 基于五种业务角色（BUYER、FARMER、EXPERT、BANK、
 *       SYSTEM_ADMIN）对写操作与敏感资源进行精细控制。</li>
 *   <li><strong>查询友好开放</strong> —— 面向游客的展示型 GET 接口（农产品、知识库、
 *       专家信息、银行产品）无需登录即可访问，降低使用门槛。</li>
 *   <li><strong>统一异常响应</strong> —— 认证失败（401）和授权拒绝（403）直接写出
 *       {@link ApiResponse} 格式的 JSON 体，与全局异常处理风格保持一致。</li>
 * </ul>
 *
 * <h2>权限矩阵概要</h2>
 * <table>
 *   <caption>接口分组与所需角色对照</caption>
 *   <tr><th>接口分组</th><th>HTTP 方法</th><th>所需角色</th></tr>
 *   <tr><td>健康检查、角色字典、静态文件、Swagger 文档</td><td>全部</td><td>公开（permitAll）</td></tr>
 *   <tr><td>Auth 登录/注册</td><td>POST</td><td>公开（permitAll）</td></tr>
 *   <tr><td>Admin 后台管理</td><td>全部</td><td>SYSTEM_ADMIN</td></tr>
 *   <tr><td>图片上传</td><td>POST</td><td>任意已认证角色</td></tr>
 *   <tr><td>融资银行匹配</td><td>GET</td><td>FARMER</td></tr>
 *   <tr><td>融资农户匹配</td><td>GET</td><td>BANK</td></tr>
 *   <tr><td>知识库增/改/删</td><td>POST/PUT/DELETE</td><td>EXPERT</td></tr>
 *   <tr><td>购物车</td><td>全部</td><td>BUYER</td></tr>
 *   <tr><td>订单、地址、农产品采购</td><td>全部</td><td>BUYER 或 FARMER</td></tr>
 *   <tr><td>融资申请、意向</td><td>全部</td><td>FARMER 或 BANK</td></tr>
 *   <tr><td>咨询</td><td>全部</td><td>FARMER 或 EXPERT</td></tr>
 * </table>
 *
 * <p>注意：Spring Security 的规则匹配是<strong>从上到下首次命中即停止</strong>，
 * 因此公共 GET 查询规则必须排在对应路径的写操作规则<em>之前</em>声明，否则游客
 * 的 GET 请求会被后续的 {@code .hasRole()} 规则拦截。</p>
 *
 * @author seeking
 * @since 1.0
 * @see HeaderRoleAuthenticationFilter
 * @see ApiResponse
 */
@Configuration
public class SecurityConfig {

    /**
     * 自定义请求头角色认证过滤器。
     * <p>在每次请求到达 Spring Security 主链前，解析请求头中的角色令牌，
     * 构造 {@link org.springframework.security.core.Authentication} 对象
     * 并设置到 {@link org.springframework.security.core.context.SecurityContextHolder}。</p>
     */
    private final HeaderRoleAuthenticationFilter headerRoleAuthenticationFilter;

    /**
     * Jackson 序列化工具，用于在 Security 异常处理中写出 JSON 格式的统一响应体。
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造 Security 配置实例，注入认证过滤器与 JSON 序列化器。
     *
     * @param headerRoleAuthenticationFilter 自定义 Header 角色认证过滤器（必须非 null）
     * @param objectMapper                   Spring 容器内管理的 Jackson {@link ObjectMapper} Bean
     */
    public SecurityConfig(HeaderRoleAuthenticationFilter headerRoleAuthenticationFilter, ObjectMapper objectMapper) {
        this.headerRoleAuthenticationFilter = headerRoleAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    /**
     * 构建并注册平台主安全过滤链 {@link SecurityFilterChain}。
     *
     * <h3>安全策略细节</h3>
     * <ol>
     *   <li><strong>关闭 CSRF</strong> —— 前后端分离，无 Cookie 承载认证信息，
     *       跨站请求伪造保护无适用场景。</li>
     *   <li><strong>启用 CORS</strong> —— 使用 Spring Boot 默认的 CORS 策略，
     *       实际跨域规则由 {@link org.springframework.web.cors.CorsConfigurationSource}
     *       或配置文件中的 {@code spring.web.cors} 属性控制。</li>
     *   <li><strong>无状态会话</strong> —— {@link SessionCreationPolicy#STATELESS}
     *       确保 Spring Security 不创建、不缓存 {@link jakarta.servlet.http.HttpSession}，
     *       每次请求独立认证。</li>
     *   <li><strong>异常处理</strong> —— 认证入口点（401）与拒绝访问点（403）
     *       均直接写出 JSON 响应，绕过 Controller 切面。</li>
     *   <li><strong>OPTIONS 预检放行</strong> —— 浏览器 CORS 预检请求无条件通过，
     *       避免跨域复杂请求被权限规则拦截。</li>
     *   <li><strong>规则顺序</strong> —— 先声明公共资源与公开 GET 查询，
     *       再逐层收紧写操作与角色限定路径，最后兜底 {@code anyRequest().authenticated()}。</li>
     *   <li><strong>过滤器插入</strong> —— 在 {@link UsernamePasswordAuthenticationFilter}
     *       之前插入自定义 {@link HeaderRoleAuthenticationFilter}，确保角色信息
     *       在正式授权判断前已就绪。</li>
     * </ol>
     *
     * @param http Spring Security 的 HTTP 安全构建器，由框架自动注入
     * @return 组装完成的 {@link SecurityFilterChain} Bean，供 FilterChainProxy 使用
     * @throws Exception 当 HttpSecurity 配置或构建过程抛出异常时向上传播
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离接口不依赖 Cookie Session，关闭 CSRF 并使用无状态会话。
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 统一异常处理：401 / 403 均以 JSON 格式返回，保持与业务异常相同的响应结构。
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, ex) ->
                                writeError(response, HttpStatus.UNAUTHORIZED, "Authentication required"))
                        .accessDeniedHandler((request, response, ex) ->
                                writeError(response, HttpStatus.FORBIDDEN, "Access denied")))
                // ---------- 权限规则矩阵 ----------
                .authorizeHttpRequests(auth -> auth
                        // CORS 预检请求无条件放行。
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
                        // 登录与注册：未认证用户唯一合法的 POST 入口。
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/login", "/api/auth/register", "/api/auth/email-code").permitAll()
                        // 后台管理接口仅超级管理员可访问。
                        .requestMatchers("/api/admin/**").hasRole("SYSTEM_ADMIN")
                        // 通知接口：所有已登录用户可读取和标记自己的通知。
                        .requestMatchers("/api/notifications/**")
                                .hasAnyRole("BUYER", "FARMER", "EXPERT", "BANK", "SYSTEM_ADMIN")
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
                        // 用户注册入口（开放给未登录用户）。
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // ---- 知识库：仅专家有写权限 ----
                        .requestMatchers(HttpMethod.POST, "/api/knowledge").hasRole("EXPERT")
                        .requestMatchers(HttpMethod.PUT, "/api/knowledge/*").hasRole("EXPERT")
                        .requestMatchers(HttpMethod.DELETE, "/api/knowledge/*").hasRole("EXPERT")
                        // 专家接口仅限专家角色访问。
                        .requestMatchers("/api/experts/**").hasRole("EXPERT")
                        // 用户资料管理：买家、农户、专家、银行均可操作自身信息。
                        .requestMatchers("/api/users/**").hasAnyRole("BUYER", "FARMER", "EXPERT", "BANK", "SYSTEM_ADMIN")
                        // 收货地址：仅买家与农户需要。
                        .requestMatchers("/api/addresses/**").hasAnyRole("BUYER", "FARMER")
                        // 订单操作：货源的增改删仅农户（发布方）可执行，买家只能浏览（GET 已在上方公开）。
                        .requestMatchers(HttpMethod.POST, "/api/trade/orders").hasRole("FARMER")
                        .requestMatchers(HttpMethod.PUT, "/api/trade/orders/*").hasRole("FARMER")
                        .requestMatchers(HttpMethod.PATCH, "/api/trade/orders/*/status").hasRole("FARMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/trade/orders/*").hasRole("FARMER")
                        .requestMatchers("/api/trade/orders/**").hasAnyRole("BUYER", "FARMER")
                        // 购物车：买家的专属功能。
                        .requestMatchers("/api/trade/shopping-cart/**").hasRole("BUYER")
                        // 农产品采购记录：买家购买、农户出售，双方均可查看。
                        .requestMatchers("/api/trade/purchases/**").hasAnyRole("BUYER", "FARMER")
                        // 银行产品管理：仅银行角色。
                        .requestMatchers("/api/finance/banks/**").hasRole("BANK")
                        // 知识库浏览与收藏：农户学习、专家维护。
                        .requestMatchers("/api/knowledge/**").hasAnyRole("FARMER", "EXPERT")
                        // 农技咨询：农户提问、专家回答。
                        .requestMatchers("/api/consultation/**").hasAnyRole("FARMER", "EXPERT")
                        // 融资申请流程：农户发起和补充材料，银行负责最终审批。
                        .requestMatchers(HttpMethod.POST, "/api/finance/applications").hasRole("FARMER")
                        .requestMatchers(HttpMethod.PATCH, "/api/finance/applications/*/materials").hasRole("FARMER")
                        .requestMatchers(HttpMethod.PATCH, "/api/finance/applications/*/status").hasRole("BANK")
                        .requestMatchers("/api/finance/applications/**").hasAnyRole("FARMER", "BANK")
                        // 融资意向沟通：农户与银行双向。
                        .requestMatchers("/api/finance/intentions/**").hasAnyRole("FARMER", "BANK")
                        // 兜底规则：未明确匹配的路径必须经过认证。
                        .anyRequest().authenticated())
                // 将自定义 Header 角色认证过滤器插入 UsernamePasswordAuthenticationFilter 之前，
                // 确保请求到达授权判断时 SecurityContextHolder 中已有角色信息。
                .addFilterBefore(headerRoleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 向客户端写出 Security 异常对应的 JSON 错误响应。
     *
     * <p>Spring Security 的异常处理发生在 Filter 层，在 DispatcherServlet 之前，
     * 因此不会进入 {@code @RestControllerAdvice} 全局异常切面。
     * 这里手动调用 {@link ObjectMapper} 将 {@link ApiResponse} 序列化为 JSON，
     * 确保认证/授权类异常的响应体与业务异常格式完全一致。</p>
     *
     * @param response 当前 HTTP 响应对象（由 Security 过滤器链传入）
     * @param status   要写入的 HTTP 状态码（通常为 401 或 403）
     * @param message  面向客户端的错误描述信息
     * @throws java.io.IOException 当写入响应输出流失败时抛出（由调用方宿主的 Lambda 向上传播）
     */
    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws java.io.IOException {
        // Security 异常不经过 ControllerAdvice，这里手动写出统一响应结构。
        response.setStatus(status.value());
        // 必须显式设置 Content-Type，否则 Spring Security 默认写出 text/plain。
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(status.value(), message));
    }
}
