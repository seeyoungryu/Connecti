package com.seeyoungryu.connecti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//암호화 설정 클래스(비밀번호 암호화)
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }
}





/*
encodePassword 메서드 -> 반환되는 BCryptPasswordEncoder 객체를 빈으로 등록하여 서비스 레이어에서 암호화에 사용할 수 있게 함.
ㄴ> Spring Security는 이를 사용하여 비밀번호를 암호화하고 검증함 (이 메서드가 PasswordEncoder` 인터페이스를 구현하고 있으므로)


`BCryptPasswordEncoder` 클래스는 `PasswordEncoder` 인터페이스를 구현하고 있으며, 암호화를 위한 두 가지 핵심 메서드를 제공.
-encode(CharSequence rawPassword): 입력된 비밀번호를 암호화된 형태로 변환
-matches(CharSequence rawPassword, String encodedPassword): 입력된 비밀번호와 암호화된 비밀번호를 비교해 일치 여부를 확인
 */