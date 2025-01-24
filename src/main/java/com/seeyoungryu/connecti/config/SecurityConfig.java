package com.seeyoungryu.connecti.config;

import com.seeyoungryu.connecti.config.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



/*
JwtTokenFilter와 같이 HTTP 요청/응답의 흐름에 영향을 주는 구성 요소는 SecurityConfig(Spring Security 기반 애플리케이션에서 요청/응답 흐름을 제어하는 역할을 하는 클래스)에서 관리하는 것이 표준적이고 직관적임.
(Spring Security가 애플리케이션의 보안 정책을 정의하고 관리하는 핵심 역할을 하기 때문
SecurityConfig는 HTTP 요청이 애플리케이션(컨트롤러~)에 도달하기 전에 보안 필터 체인을 통해 요청/응답 흐름을 관리 (HTTP 요청/응답의 흐름에 영향)- 이 과정에서 인증(Authentication)과 권한(Authorization) 검증이 이루어지고, 요청이 허용되거나 거부됨.)
 */


//SecurityConfig : 보안 필터 및 HTTP 보안 설정 클래스
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(userDetailsService, secretKey);
    }
    // * 이 메서드와 같이 HTTP 요청/응답의 흐름에 영향을 주는 구성 요소는 SecurityConfig에서 관리하는 것이 표준적이고 직관적임

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // 최근 권장 방식으로 변경 (+ 람다 -> 참조형)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/login", "/api/v1/users/join").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        //SessionCreationPolicy를 설정하면 세션 기반 인증 또는 토큰 기반 인증 방식이 결정됨
                        //위 설정은 Spring Security가 Stateless(무상태)로 작동하도록 지정함.
                        //이 설정이 없다면, Spring Security는 기본적으로 세션을 생성하고 요청 간에 인증 상태를 유지하려고 함.
                        //Stateless 설정은 HTTP 요청을 각각 독립적인 것으로 취급하며, 매 요청마다 인증 정보를 확인.
                )
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



/*
순환참조 fix - 수정 내역 (설정값 분리)
SecurityConfig 애서는 JWT 및 보안 설정,JwtTokenFilter 설정
 */


