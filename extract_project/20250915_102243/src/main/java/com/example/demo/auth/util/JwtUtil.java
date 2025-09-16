package com.example.demo.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.Date;

public class JwtUtil {

    // 秘钥，可换成更安全的方式存储
    private static final String SECRET_KEY = "MySuperSecretKeyForJWTTokenGenerationThatIsMuchLongerAndMoreSecure123456789";

    // token 有效期：1 天
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;

    // 生成 JWT
    public static String generateToken(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 解析 JWT
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
                    }