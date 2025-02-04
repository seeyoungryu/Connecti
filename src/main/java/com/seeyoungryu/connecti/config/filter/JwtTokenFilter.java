package com.seeyoungryu.connecti.config.filter;

import com.seeyoungryu.connecti.service.JwtAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtTokenFilter: JWT 인증 필터
 * - HTTP 요청에서 JWT를 추출하고 검증 서비스에 위임
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtAuthenticationService jwtAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 헤더가 없거나 Bearer로 시작하지 않는 경우
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response); // JWT가 없으면 다음 필터로 진행
            return;
        }

        String token = header.substring(7); // "Bearer " 부분 제거
        try {
            // JWT 검증 및 사용자 이름 추출
            String username = jwtAuthenticationService.validateAndExtractUsername(token);

            // 인증 객체 생성 및 SecurityContext 설정
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, null
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            log.error("JWT Token validation failed: {}", e.getMessage());
            SecurityContextHolder.clearContext(); // 인증 실패 시 SecurityContext 초기화
        }

        chain.doFilter(request, response); // 다음 필터로 진행
    }
}
