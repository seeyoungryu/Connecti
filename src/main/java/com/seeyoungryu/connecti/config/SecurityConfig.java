package com.seeyoungryu.connecti.config;

import com.seeyoungryu.connecti.config.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    /*
    SecurityFilterChain을 빈으로 직접 정의하여 보안 설정을 구성
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // CSRF 비활성화
                // .csrf(csrf -> csrf.disable())  : 메서드의 인자로 전달되어 CSRF 설정을 끄는 작업을 수행 (람다식)
                /* 기존에는 메서드 체이닝 방식을 씀 (csrf() 메서드를 호출한 후에, 그 결과로 반환된 객체에 대해 다시 disable()을 호출하여 체이닝 방식으로 이어서 씀)
                   현재 사용하는 방식: csrf().disable()은 메서드 체이닝에서 disable()로 호출하지 않고, << 람다 표현식 안에서 disable() 메서드를 사용>> 하는 것이 맞음
                */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/login", "/api/v1/users/join").permitAll() // 로그인과 회원가입은 모두 접근 가능
                        .requestMatchers("/api/v1/users/me").authenticated() // `me` 엔드포인트는 인증이 필요    //requestMatchers()로 URL 패턴을 지정
                        .anyRequest().authenticated() // 나머지 모든 요청은 인증이 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /*
   AuthenticationManager 빈 등록: AuthenticationManager를 직접 빈으로 등록해 사용할 수 있도록 설정
    */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
