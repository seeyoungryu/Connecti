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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // 최근 권장 방식으로 변경 (+ 람다 -> 참조형)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/login", "/api/v1/users/join").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



/*
순환참조 fix - 수정 내역 (설정값 분리)
SecurityConfig 애서는 JWT 및 보안 설정,JwtTokenFilter 설정
 */