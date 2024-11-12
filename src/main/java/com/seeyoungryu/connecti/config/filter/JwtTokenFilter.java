package com.seeyoungryu.connecti.config.filter;

import com.seeyoungryu.connecti.service.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    //    private final UserService userService;
    private final UserDetailsService userDetailsService; // UserService 대신 UserDetailsService 주입
    private final String secretKey;


     /*
        1. 토큰 추출
        2. 토큰 유효성 검사(유효하지 않으면 바로 종료)
        3. 토큰에서 사용자 이름 추출
        4. 사용자 이름이 유효한지 검사 (이 때, 해당 사용자 정보를 로드하고 인증 처리)
         */


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Error occurs while getting header. Header is null or doesn't start with Bearer");
        } else {
            try {

                 /*
                 1. 토큰 추출 ("Bearer " 이후의 토큰을 추출)
                  */
                final String token = header.split(" ")[1].trim();

                /*
                2. 토큰 유효성 검사 (토큰이 유효하지 않으면 -> 해당 로그를 남기고 이후 코드는 실행되지 않음)
                 */
                if (!JwtTokenUtils.isTokenExpired(token, secretKey)) {
                    log.error("Invalid JWT token - secretKey is expired");
                } else {
                    /*
                    3. 사용자 이름 추출 (유효한 토큰일 경우)
                     */
                    String userName = JwtTokenUtils.getUsernameFromToken(token, secretKey);

                    /* 4. 사용자 정보 조회
                          , 유효성 검사 (유효한 사용자라면 SecurityContextHolder에 인증 정보를 설정함)
                     */

                    //User userDetails = user.loadUserByUsername(userName);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);    //SecurityContextHolder에 인증 정보를 설정 (setAuthentication)
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("Authorization header format is invalid: {}", header);
            } catch (RuntimeException e) {
                log.error("Error while processing JWT token", e);
            }
        }

        // 마지막에 한 번만 필터 체인 호출 ( " SINGLE EXIT STYLE " )
        filterChain.doFilter(request, response);
    }
}


/*
<순환참조 해결> -> UserService 대신 UserDetailsService 주입
UserDetailsService는 UserService가 구현하는 인터페이스이므로 기존 로직에 영향을 주지 않으면서도 순환 참조 문제를 해결
UserDetailsService의 loadUserByUsername 메서드는 사용자 인증을 위한 표준 인터페이스를 제공하므로,
이 인터페이스를 구현한 UserService가 UserDetails 타입의 사용자 정보를 반환하게 설정하면 자연스럽게 Spring Security와 호환됨
(UserDetails의 인터페이스 구현을 이용해 필요한 메서드(getUsername, getPassword, getAuthorities) 호출 가능)
 */