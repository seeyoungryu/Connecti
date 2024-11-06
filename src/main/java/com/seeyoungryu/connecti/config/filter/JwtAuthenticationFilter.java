package com.seeyoungryu.connecti.config.filter;

import com.seeyoungryu.connecti.service.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;

    public JwtAuthenticationFilter(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Authorization 헤더에서 JWT 토큰 추출
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // 'Bearer ' 부분을 제거하고 토큰만 남김
            String username = jwtTokenUtils.extractUsername(token);  // JWT에서 사용자 이름 추출

            // 사용자 이름이 유효하고, 현재 인증이 안 되어 있을 경우
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 토큰에서 권한 정보를 가져와 Authentication 객체 생성
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, jwtTokenUtils.getAuthorities(token));
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 객체 설정
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        }
        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

}
