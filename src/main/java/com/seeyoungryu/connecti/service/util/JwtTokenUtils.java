package com.seeyoungryu.connecti.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JwtTokenUtils: JSON Web Token (JWT) 관련 작업을 처리하는 유틸리티 클래스
 */
@Component
public class JwtTokenUtils {

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String secretKey, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token, String secretKey) {
        Date expiration = extractClaims(token, secretKey).getExpiration();
        return expiration.before(new Date());
    }

    public String getUsername(String token, String secretKey) {
        return extractClaims(token, secretKey).get("username", String.class);
    }

    private Claims extractClaims(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
