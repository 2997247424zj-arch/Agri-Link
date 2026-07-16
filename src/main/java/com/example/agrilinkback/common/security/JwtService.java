package com.example.agrilinkback.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/**
 * JWT 签发与校验服务。
 *
 * <p>Claims 约定：
 * <ul>
 *   <li>{@code sub} — 用户名</li>
 *   <li>{@code role} — 角色编码（BUYER/FARMER/...）</li>
 * </ul>
 */
@Service
public class JwtService {

    public static final String CLAIM_ROLE = "role";

    private final SecretKey secretKey;
    private final long expireMillis;

    public JwtService(JwtProperties properties) {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expireMillis = Math.max(1, properties.getExpireHours()) * 3_600_000L;
    }

    /**
     * 为指定用户签发访问令牌。
     */
    public String generateToken(String userName, UserRole role) {
        Instant now = Instant.now();
        Instant expireAt = now.plusMillis(expireMillis);
        return Jwts.builder()
                .subject(userName)
                .claim(CLAIM_ROLE, role.code())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析并校验 JWT；签名无效、过期或格式错误时返回 empty。
     */
    public Optional<Claims> parseClaims(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token.trim())
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
