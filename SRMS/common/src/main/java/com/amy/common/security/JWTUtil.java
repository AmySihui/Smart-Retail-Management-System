package com.amy.common.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;


public class JWTUtil {

    // 用于签名的密钥（建议真实项目中从配置文件读取）
    private static final String SECRET = "SuperSecretKeyForSRMSProject1234567890!"; // >=256 bit
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 默认过期时间 1 小时
    private static final long EXPIRATION_MS = 3600_000;

    // 生成 JWT token（带 userId）
    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer("SRMS")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 校验 token，返回 userId，如果无效则抛出异常
    public static String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // userId
    }
}
