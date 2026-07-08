package com.example.agrilinkback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Web 层全局配置类 —— 主要负责跨域资源共享（CORS）。
 *
 * <h3>背景</h3>
 * <p>在基于 Vue3 前、后端分离的架构下，前端开发服务器（如 Vite）和后端
 * Spring Boot 服务通常运行在不同端口上。浏览器同源策略会阻止此类跨域
 * HTTP 请求，因此需要在后端统一配置 CORS 规则来允许合法跨域访问。</p>
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>向 Spring 容器注册一个 {@link CorsFilter} Bean，对所有进入的
 *       请求进行 CORS 头处理。</li>
 *   <li>统一管理跨域策略，避免业务模块各自配置导致不一致。</li>
 * </ul>
 *
 * <h3>注意事项</h3>
 * <p>当前配置放开了所有来源（{@code addAllowedOriginPattern("*")}）、
 * 所有方法与所有请求头，同时启用了凭据传递
 * （{@code allowCredentials = true}）。这种模式适用于开发联调与内部
 * 测试环境，但部署到公网或生产环境时应收紧策略，建议限定明确的域名、
 * 方法和头字段以降低 CSRF 等安全风险。</p>
 *
 * @author Agri-Link Team
 * @see org.springframework.web.cors.CorsConfiguration
 * @see org.springframework.web.filter.CorsFilter
 */
@Configuration
public class WebConfig {

    /**
     * 注册全局 CORS 过滤器 Bean。
     *
     * <p>该过滤器会在请求到达业务逻辑之前拦截并补全响应中的
     * {@code Access-Control-Allow-*} 系列头，使前端能够正常接收
     * 跨域响应。</p>
     *
     * <h4>CORS 策略细节</h4>
     * <ul>
     *   <li><b>来源：</b>使用 {@code addAllowedOriginPattern("*")}
     *       通配所有来源，配合 {@code allowCredentials=true} 时
     *       不能用 {@code addAllowedOrigin("*")}，否则浏览器会拒绝
     *       凭据类请求。</li>
     *   <li><b>方法：</b>允许全部 HTTP 方法（GET / POST / PUT /
     *       DELETE / PATCH / OPTIONS 等）。</li>
     *   <li><b>头：</b>允许所有自定义请求头（如 Authorization、
     *       Content-Type、X-Requested-With 等）。</li>
     *   <li><b>凭据：</b>允许携带 Cookie 和 Authorization 等凭据
     *       信息。</li>
     * </ul>
     *
     * @return 配置好 CORS 规则的 {@link CorsFilter} 实例，由 Spring
     *         容器管理其生命周期
     * @see UrlBasedCorsConfigurationSource#registerCorsConfiguration(String, CorsConfiguration)
     */
    @Bean
    public CorsFilter corsFilter() {
        // 构建 CORS 配置对象，逐项设定允许的规则
        CorsConfiguration config = new CorsConfiguration();
        // 允许任意来源发起跨域请求（开发期宽松策略）
        config.addAllowedOriginPattern("*");
        // 允许所有 HTTP 方法
        config.addAllowedMethod("*");
        // 允许携带任意请求头
        config.addAllowedHeader("*");
        // 允许浏览器在跨域请求中携带凭据（Cookie / Auth Header）
        config.setAllowCredentials(true);

        // 将 CORS 规则注册到路径模式 "/**" 上，使所有接口统一应用同一套策略，
        // 避免各模块因分散配置导致规则不一致或遗漏
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
