package com.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtTokenProvider(@Value("${blog.jwt.secret}") String secret,
                            @Value("${blog.jwt.expiration}") long expiration) {
        byte[] keyBytes = new byte[32];
        byte[] srcBytes = secret.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(srcBytes, 0, keyBytes, 0, Math.min(srcBytes.length, 32));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    public String generateToken(Long adminId) {
        return buildToken(adminId, "admin", expiration);
    }

    public String generateUserToken(Long userId) {
        return buildToken(userId, "user", expiration * 36); // 36x expiration = 72 hours
    }

    private String buildToken(Long id, String type, long expMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expMs))
                .signWith(secretKey)
                .compact();
    }

    public record TokenInfo(Long id, String type) {}

    public TokenInfo parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new TokenInfo(
                Long.parseLong(claims.getSubject()),
                claims.get("type", String.class)
        );
    }

    public Long getAdminIdFromToken(String token) {
        return parseToken(token).id();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
