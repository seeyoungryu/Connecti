package com.seeyoungryu.connecti.config.filter;

import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.service.UserService;
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
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
                if (!JwtTokenUtils.validate(token, null, secretKey)) {
                    log.error("Invalid JWT token");
                } else {

                    /*
                    3. 사용자 이름 추출 (유효한 토큰일 경우)
                     */
                    String userName = JwtTokenUtils.getUsername(token, secretKey);

                    /* 4. 사용자 정보 조회
                          , 유효성 검사 (유효한 사용자라면 SecurityContextHolder에 인증 정보를 설정함)
                     */
                    User userDetails = userService.loadUserByUsername(userName);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);    //SecurityContextHolder에 인증 정보를 설정 (setAuthentication)
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("Authorization header format is invalid: {}", header);
            } catch (Exception e) {
                log.error("Error while processing JWT token", e);
            }
        }

        // 마지막에 한 번만 필터 체인 호출 ( " SINGLE EXIT STYLE " )
        filterChain.doFilter(request, response);
    }
}
