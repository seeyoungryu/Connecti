package com.seeyoungryu.connecti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthenticationConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/join").permitAll()  // 특정 경로는 접근 허용
                        .anyRequest().authenticated()  // 나머지는 인증 필요
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션을 사용하지 않도록 설정 (이렇게 설정하면 세션을 사용하지 않는 방식(주로 JWT와 같은 토큰 인증 방식에서 사용)으로 보안을 구성할 수 있음
                );

        return http.build();
    }
}


//람다 -> 참조식
// 람다: .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
// 참조식으로 변경: .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
