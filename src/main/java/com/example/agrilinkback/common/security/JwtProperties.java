package com.example.agrilinkback.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置属性，绑定 {@code application.security.jwt.*}。
 */
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    /**
     * HMAC 签名密钥原文。生产环境必须通过环境变量覆盖，长度建议 ≥ 32 字节。
     */
    private String secret = "AgriLink-Jwt-Secret-Key-ChangeMe-32B!";

    /**
     * 访问令牌有效期（小时）。
     */
    private long expireHours = 24;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireHours() {
        return expireHours;
    }

    public void setExpireHours(long expireHours) {
        this.expireHours = expireHours;
    }
}
