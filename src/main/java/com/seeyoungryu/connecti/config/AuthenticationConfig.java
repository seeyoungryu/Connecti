package com.seeyoungryu.connecti.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    @Lazy
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}




/*
설명
AuthenticationConfig: BCryptPasswordEncoder 및 AuthenticationManager를 빈으로 정의
SecurityConfig: JWT 및 보안 설정을 담당하며, JwtTokenFilter를 설정하고 세션을 사용하지 않도록 구성
UserService: 순환 참조 방지를 위해 PasswordEncoder에 @Lazy를 적용
 */