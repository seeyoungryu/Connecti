package com.seeyoungryu.connecti.config;

import com.seeyoungryu.connecti.config.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    pring Security 5.7 이후로 WebSecurityConfigurerAdapter가 deprecated 되었습니다. Spring Security 6.x 이상에서는 WebSecurityConfigurerAdapter를 사용하지 않고 SecurityFilterChain을 빈으로 직접 정의하여 보안 설정을 구성합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/login", "/api/v1/users/join").permitAll() // 로그인과 회원가입은 모두 접근 가능
                        .requestMatchers("/api/v1/users/me").authenticated() // `me` 엔드포인트는 인증이 필요    //antMatchers()가 제거되고 requestMatchers()로 URL 패턴을 지정합니다.
                        .anyRequest().authenticated() // 나머지 모든 요청은 인증이 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    /*
    csrf() 설정: csrf().disable()은 더 이상 메서드 체이닝에서 disable()로 호출하지 않고, 람다 표현식 안에서 disable() 메서드를 사용합니다.
     */

    /*
    authorizeRequests 대신 authorizeHttpRequests: URL 접근 권한을 설정할 때 authorizeRequests() 대신 authorizeHttpRequests()를 사용합니다.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 빈 등록 (필요 시)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
    AuthenticationManager 빈 등록: AuthenticationManager를 직접 빈으로 등록해 사용할 수 있도록 설정합니다.
     */
}
