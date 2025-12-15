package com.itzixi.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private Long expiration; // 秒
    
    @Value("${jwt.refresh-expiration:604800}")
    private Long refreshExpiration; // 刷新token过期时间，秒
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * 生成访问Token
     */
    public String createToken(String username) {
        return createToken(username, expiration);
    }
    
    /**
     * 生成刷新Token
     */
    public String createRefreshToken(String username) {
        return createToken(username, refreshExpiration);
    }
    
    /**
     * 生成Token
     */
    private String createToken(String username, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("created", new Date());
        
        Date expirationDate = Date.from(
            LocalDateTime.now()
                .plusSeconds(expiration)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        );
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("获取用户名失败", e);
            return null;
        }
    }
    
    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证Token是否有效（包含用户名验证）
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return username.equals(tokenUsername) && validateToken(token);
    }
    
    /**
     * 刷新Token
     */
    public String refreshToken(String oldToken) {
        try {
            Claims claims = getClaimsFromToken(oldToken);
            String username = claims.getSubject();
            
            // 检查是否在刷新窗口期内
            Date tokenCreated = (Date) claims.get("created");
            Date now = new Date();
            
            long tokenAge = now.getTime() - tokenCreated.getTime();
            long refreshWindow = refreshExpiration * 1000; // 转换为毫秒
            
            if (tokenAge < refreshWindow) {
                return createToken(username);
            } else {
                throw new IllegalArgumentException("Token超出刷新窗口期");
            }
        } catch (Exception e) {
            log.error("Token刷新失败", e);
            throw new IllegalArgumentException("Token刷新失败: " + e.getMessage());
        }
    }
    
    /**
     * 判断Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("检查Token过期时间失败", e);
            return true;
        }
    }
    
    /**
     * 从Token中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    /**
     * 从Token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 获取Token剩余有效时间（秒）
     */
    public long getRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            return Math.max(0, (expiration.getTime() - now.getTime()) / 1000);
        } catch (Exception e) {
            log.error("获取Token剩余时间失败", e);
            return 0;
        }
    }
} 