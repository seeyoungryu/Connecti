package com.seeyoungryu.connecti.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



/*
순환참조 fix - 수정 내역 (설정값 분리)
BCryptPasswordEncoder 및 AuthenticationManager ~ 빈으로 정의
 */


@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    //    @Lazy
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {     //passwordEncoder 현재 클래스에서 정의
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}





/* (참고)
AuthenticationConfiguration은 Spring Security에서 제공하는 클래스 (Spring Security가 AuthenticationManager를 자동으로 구성할 수 있도록 돕는 역할)
- AuthenticationManager를 생성하고 설정하는 데 필요한 구성 요소를 제공 (이 클래스는 Spring Security가 AuthenticationManager를 생성하고 설정을 위한 여러 기능을 제공합니다.)
Spring Security 설정에서 AuthenticationManager를 빈으로 직접 정의하기 위해
AuthenticationConfiguration을 의존성으로 주입하여 getAuthenticationManager() 메서드를 호출하는 방식으로 AuthenticationManager 빈을 생성


authenticationConfiguration.getAuthenticationManager():
AuthenticationConfiguration 객체의 getAuthenticationManager() 메서드 ~ AuthenticationManager 빈을 생성
-> 커스텀 AuthenticationManager를 정의하지 않고 Spring Security의 기본 AuthenticationManager 구성을 사용할 수 있음
 */


/*
Security 설정을 SecurityConfig와 AuthenticationConfig로 분리했을 때 순환 참조가 해결된 이유
: 클래스 간의 빈 의존성을 관리하는 방식이 변경되었음.
* Spring의 빈 생명 주기와 초기화 과정에서 필요하지 않은 순환 의존성을 줄이게 됨

<의존성의 초기화 시점 관리>
: AuthenticationManager와 BCryptPasswordEncoder는 인증 관련 빈임
(일반적으로 애플리케이션의 인증을 지원하기 위해 존재하지만, JWT 필터와 같은 보안 설정과는 직접적인 상호작용이 없음
 -> AuthenticationConfig로 분리하여 인증 관련 빈을 독립적으로 초기화하도록 구성하여 순환참조 해결

<빈 생성 순서에 따른 순환 참조 회피>
: Spring은 의존성 주입을 설정된 순서대로 빈을 초기화함.
분리된 설정 클래스가 각 빈을 필요한 시점에 명확히 주입할 수 있도록 하여,
빈 생성 과정에서 UserDetailsService와 AuthenticationManager 간의 참조가 순환되지 않음
(SecurityConfig와 AuthenticationConfig를 분리하면서 각 빈의 초기화 시점이 명확해지고
, 직접적인 참조 관계가 해소되면서 순환 참조 문제가 해결됨)
 */