package com.seeyoungryu.connecti.config.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//
//
//@Slf4j
//@RequiredArgsConstructor
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    private final UserService userService;
//
//    private final String secretKey;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain)
//            throws ServletException, IOException {
//        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (!header.startsWith("Bearer ")) {
//            log.warn("Authorization Header does not start with Bearer");
//            chain.doFilter(request, response);
//            return;
//        }
//
//        final String token = header.split(" ")[1].trim();
//        User userDetails = userService.loadUserByUsername(JwtTokenUtils.getUsername(token, secretKey));
//
//        if (!JwtTokenUtils.validate(token, userDetails, secretKey)) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                userDetails, null,
//                userDetails.getAuthorities()
//        );
//
//        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        chain.doFilter(request, response);
//    }
//
//}


@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;

    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!header.startsWith("Bearer ")) {
            log.warn("Authorization Header does not start with Bearer");
            chain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        User userDetails = userService.loadUserByUsername(JwtTokenUtils.getUsername(token, secretKey));

        if (!JwtTokenUtils.validate(token, userDetails, secretKey)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

}



/*
두 코드에서 가장 큰 차이점은 JWT 토큰 기반 인증을 구현하는 방식과 Spring Security의 설정 방식입니다. 아래에 각 코드의 차이점과 이를 통해 이해해야 할 내용을 상세히 설명드릴게요.

1. Spring Security 설정 방식
사용자 코드 (AuthenticationConfig)
SecurityFilterChain을 이용한 람다 스타일 설정:
SecurityFilterChain을 사용하여 HttpSecurity 설정을 구성합니다. 람다 스타일의 참조식(AbstractHttpConfigurer::disable)으로 간결하게 설정되어 있습니다.
세션 정책: SessionCreationPolicy.STATELESS로 설정해 세션을 사용하지 않는 방식으로 보안을 구성하고 있습니다. 이 설정은 주로 JWT 토큰 기반 인증에서 사용되며, 서버가 세션을 생성하지 않으므로 확장성에서 이점을 제공합니다.
허용 경로 설정: /api/v1/users/join와 /api/v1/users/login 경로는 인증 없이 접근을 허용하고, 나머지 경로는 인증이 필요하도록 설정합니다.
강사 코드 (AuthenticationConfiguration)
WebSecurityConfigurerAdapter 사용:
Spring Security 설정을 WebSecurityConfigurerAdapter를 상속한 클래스로 설정합니다. WebSecurityConfigurerAdapter는 보안 설정을 메소드 configure로 나눠 구성할 수 있게 해주며, 세부적인 설정을 관리하기에 용이합니다.
세션 정책: 마찬가지로 SessionCreationPolicy.STATELESS로 세션을 사용하지 않는 방식으로 설정하고 있습니다.
추가 설정:
정적 리소스 무시: configure(WebSecurity web) 메서드를 사용해 /favicon.ico, /*.css, /*.js 등의 리소스를 보안 필터에서 제외하고 있습니다.
추가 허용 경로: /api/*/users/join, /api/*/users/login 경로를 허용하고 있습니다.
2. JWT 토큰 기반 인증 필터 추가
사용자 코드 (AuthenticationConfig)
사용자 코드에서는 JWT 토큰 필터가 구현되어 있지 않습니다. JWT를 통한 인증 및 권한 부여를 위해서는 별도의 필터(JwtTokenFilter 등)를 만들어서 Security 필터 체인에 추가해 주어야 합니다.
추가할 작업:
JwtTokenFilter를 구현하고, UsernamePasswordAuthenticationFilter 전에 추가하여 JWT 토큰을 해석하고 사용자 인증을 수행하도록 해야 합니다.
강사 코드 (AuthenticationConfiguration)
JWT 필터 추가: 강사 코드에서는 JwtTokenFilter가 추가되어 있습니다. JwtTokenFilter는 UsernamePasswordAuthenticationFilter 전에 위치시켜, 모든 요청에 대해 JWT 토큰을 검사하여 인증을 수행하게 합니다.
필터 동작: 이 필터는 요청 헤더에서 JWT 토큰을 추출하고, 이를 검증한 후 사용자 정보를 SecurityContext에 저장하는 역할을 합니다.
3. BCryptPasswordEncoder 설정
강사 코드에서는 BCryptPasswordEncoder를 사용하여 암호화를 설정하고 있으며, 이를 UserService와 함께 AuthenticationManagerBuilder에 등록합니다.
AuthenticationManagerBuilder를 통해 userService에서 제공하는 UserDetailsService와 BCryptPasswordEncoder를 연결하여 사용자 인증을 처리할 수 있도록 합니다.
사용자 코드에는 BCryptPasswordEncoder 설정이 없습니다. 사용자 인증이 필요할 경우 BCryptPasswordEncoder를 빈으로 등록하고 서비스 클래스에서 사용해야 합니다.
정리
해야 할 작업:
1. JWT 토큰 필터 구현 및 추가:

JwtTokenFilter를 구현하여 요청 헤더에서 JWT 토큰을 읽고 검증하는 로직을 추가하세요.
SecurityFilterChain 설정에서 UsernamePasswordAuthenticationFilter 앞에 JwtTokenFilter를 추가해야 합니다.
BCryptPasswordEncoder 설정:

비밀번호 암호화를 위해 BCryptPasswordEncoder를 빈으로 등록하세요.
UserService 클래스에서 이를 사용해 비밀번호를 암호화 및 검증하도록 수정해야 합니다.
정적 리소스 무시 설정 (선택 사항):

필요에 따라, 강사 코드처럼 정적 리소스(/*.css, /*.js 등)에 대해 보안 필터를 무시하도록 설정할 수 있습니다.
알아야 할 개념:

SecurityFilterChain과 WebSecurityConfigurerAdapter 차이: 최근 Spring Security에서는 WebSecurityConfigurerAdapter의 사용을 권장하지 않고 SecurityFilterChain 설정 방식을 권장합니다.
JWT 토큰 기반 인증: 세션을 사용하지 않고, 요청마다 클라이언트가 JWT 토큰을 포함해 서버로 인증 정보를 보내는 방식입니다.
BCryptPasswordEncoder: 비밀번호 암호화 방식 중 하나로, 보안성이 높아 많이 사용됩니다.
profile

 */