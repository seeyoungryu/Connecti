package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.service.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * JwtAuthenticationService: JWT 인증 및 검증을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final JwtTokenUtils jwtTokenUtils;

    @Value("${jwt.secret-key}")
    private String secretKey;

    /**
     * JWT 검증 및 사용자 이름 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String validateAndExtractUsername(String token) {
        if (jwtTokenUtils.isTokenExpired(token, secretKey)) {
            throw new IllegalStateException("Token is expired");
        }
        return jwtTokenUtils.getUsername(token, secretKey);
    }
}
