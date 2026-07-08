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
 * <p>该过滤器在每次 HTTP 请求到达时，从请求头 {@code X-User-Role} 中提取用户角色编码，
 * 将其转换为 Spring Security 的 {@link UsernamePasswordAuthenticationToken} 并注入到
 * {@link SecurityContextHolder} 上下文中，从而让下游的权限校验（如
 * {@code @PreAuthorize("hasRole('BUYER')")}）能够正确匹配。
 *
 * <h3>设计背景</h3>
 * <p>当前实训项目采用简化的认证方案：前端登录后直接在请求头中携带角色信息，
 * 便于前后端联调和 MockMvc 集成测试，无需完整的 OAuth2/JWT 令牌链路。
 * 在生产环境中应替换为正式的认证机制（如 Spring Security + JWT）。</p>
 *
 * <h3>工作流程</h3>
 * <ol>
 *   <li>从请求头 {@code X-User-Role} 读取角色编码字符串</li>
 *   <li>通过 {@link UserRole#fromCode(String)} 将编码映射为 {@link UserRole} 枚举</li>
 *   <li>若映射成功，构造对应的认证令牌并设置到安全上下文</li>
 *   <li>放行请求到后续过滤器链</li>
 *   <li>无论请求处理成功与否，在 {@code finally} 块中清空安全上下文，
 *       避免 Tomcat 线程池复用导致角色信息串到下一次请求</li>
 * </ol>
 *
 * <h3>请求头格式</h3>
 * <pre>{@code
 * X-User-Role: BUYER | FARMER | EXPERT | BANK | SYSTEM_ADMIN
 * }</pre>
 * 也支持缩写别名（如 {@code ADMIN} 映射为 {@code SYSTEM_ADMIN}，{@code USER} 映射为 {@code BUYER}）。
 *
 * @see UserRole
 * @see OncePerRequestFilter
 * @see SecurityContextHolder
 */
@Component
public class HeaderRoleAuthenticationFilter extends OncePerRequestFilter {

    /**
     * 携带用户角色编码的 HTTP 请求头名称。
     *
     * <p>前端在登录成功后，将当前用户的角色编码写入该请求头，
     * 后端过滤器据此构建 Spring Security 认证对象。</p>
     */
    public static final String ROLE_HEADER = "X-User-Role";

    /**
     * 过滤器的核心逻辑：从请求头中提取角色信息并注入安全上下文。
     *
     * <p>该方法由 Spring Security 过滤器链在每个 HTTP 请求时自动调用。
     * 实现遵循 {@link OncePerRequestFilter} 的约定，保证单次请求内只执行一次。</p>
     *
     * <h4>执行步骤</h4>
     * <ol>
     *   <li>读取请求头 {@code X-User-Role} 的值</li>
     *   <li>调用 {@link UserRole#fromCode(String)} 尝试将字符串映射为枚举</li>
     *   <li>若映射成功（{@link Optional#isPresent()}），构造
     *       {@link UsernamePasswordAuthenticationToken} 并存入安全上下文</li>
     *   <li>调用 {@code filterChain.doFilter()} 将请求传递给下一个过滤器</li>
     *   <li>在 {@code finally} 块中调用
     *       {@link SecurityContextHolder#clearContext()} 清理上下文</li>
     * </ol>
     *
     * @param request  HTTP 请求对象，从中读取 {@code X-User-Role} 请求头
     * @param response HTTP 响应对象，传递给后续过滤器
     * @param filterChain Spring Security 的过滤器链，用于将请求递交给下一个过滤器
     * @throws ServletException 若过滤器链处理过程中发生 Servlet 异常
     * @throws IOException      若过滤器链处理过程中发生 I/O 异常
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 从请求头获取角色编码，若能映射为合法枚举值则注入安全上下文
        UserRole.fromCode(request.getHeader(ROLE_HEADER)).ifPresent(role -> {
            // Spring Security 的 hasRole 会匹配 ROLE_ 前缀权限，这里统一转换一次。
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    role.code(),   // principal：角色编码字符串
                    null,          // credentials：该简化方案不涉及密码
                    List.of(new SimpleGrantedAuthority(role.authority())) // 权限列表：ROLE_xxx
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        try {
            // 放行请求，交由后续过滤器处理
            filterChain.doFilter(request, response);
        } finally {
            // 过滤器复用线程时必须清理上下文，避免角色信息串到下一次请求。
            SecurityContextHolder.clearContext();
        }
    }
}
