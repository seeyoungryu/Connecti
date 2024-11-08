//package com.seeyoungryu.connecti.service.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//
////도구(유틸리티) 클래스
//
//public class JwtTokenUtils {
//    public static String generateToken(String userName, String key, Long expiredTimeMs) {
//
//        Claims claims = Jwts.claims();
//        claims.put("userName", userName);
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(System.currentTimeMillis() + expiredTimeMs))
//                .signWith(getKey(key), SignatureAlgorithm.HS256)  //해시 알고리즘 사용 (참고: JwtTokenUtils 클래스에서 HS256 알고리즘을 사용하는 경우, 비밀 키는 최소 256비트(32바이트) 이상이어야 안전하게 사용할 수 있음)
//                .compact();
//    }
//
//    private static Key getKey(String key) {   //키 반환 메서드 (문자열로 된 키를 JWT 서명에 사용할 수 있는 Key 객체로 변환)
//        byte[] keyByte = key.getBytes(StandardCharsets.UTF_8);
//        return Keys.hmacShaKeyFor(keyByte);
//    }
//}
//
//
//
/// *  << claim >>
//    : JWT의 < Payload > 를 구성하는 데이터 -> JWT의에 들어갈 < 사용자 정보 > 등을 담는 역할을 함  ( *JWT 구성 : Header.Payload.Signature )
//       - generateToken() 매개변수 : 유저네임 -> claim에 추가되어 Payload에 포함됨 -> 사용자 식별에 사용됨
//        ,key(토큰 서명을 위한 암호화 키) → Signature 부분을 생성하는 데 활용
//        ,expiredTimeMs -> 토큰 만료 시간(밀리 초)을 설정하는 데 사용되며, Payload의 exp 클레임에 들어감  */


package com.seeyoungryu.connecti.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;


/*
-Spring Security와 연동
-'JSON Web Token' 을 생성,검증,사용자 정보를 추출하는 유틸 클래스(인증/권한 관리)
 */


/*
Todo : 1. 테스트 추가 (단위 테스트를 추가, 특히, JwtTokenUtils의 메서드들이 올바르게 동작하는지 검증하는 테스트가 필요함)
ㄴ> 현재 JwtUtil 클래스에서 약간 중복된 메서드들이 있고, jwtfilter 와 jwtauthentication 에서는 서비스 클래스에서 쓰는 메서드가 아닌 다른 메서드를 쓰고 있다는건데 이거 문제 생기는거 아닌지 확인 필요함

Todo: 2. 클래스 분리 고려
JwtTokenUtils 클래스가 토큰 생성, 검증, 정보 추출 등 여러 기능을 제공하다 보니 클래스가 복잡해질 수 있음
토큰 생성과 관련된 기능을 JwtTokenGenerator로, 토큰 검증과 추출은 JwtTokenValidator 또는 JwtTokenParser로 분리하면 역할이 더 명확해져 관리하기 쉬움
-> 단일 책임 원칙(SRP)을 따르는 방식
 */


@Component
public class JwtTokenUtils {


    /*
    JWT 토큰 생성 (사용자이름, 키)
     */

    public static String generateAccessToken(String userName, String key, long expiredTimeMs) {
        return generateToken(userName, expiredTimeMs, key);
    }


    // RefreshToken
    public static String generateRefreshToken(String userName, String key, long expiredTimeMs) {
        return generateToken(userName, expiredTimeMs, key);
    }

    //생성시 호출되는 메서드
    //이 메서드에서 Claims에 사용자 이름을 담고 서명함 (Claims :  JWT의 Payload에 담길 정보임, 여기서는 username을 넣는다)
    private static String generateToken(String userName, long expireTime, String key) {
        Claims claims = Jwts.claims();
        claims.put("username", userName);

        return Jwts.builder() // JWT 생성 시작
                .setClaims(claims) // JWT에 담을 사용자 정보(Claims)를 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) // JWT 발급 시각 설정
                .setExpiration(new Date(System.currentTimeMillis() + expireTime)) // JWT 만료 시각 설정
                .signWith(getKey(key), SignatureAlgorithm.HS256) // 비밀 키와 서명 알고리즘(HS256)으로 서명하여 보안 강화
                .compact(); // JWT를 문자열 형태로 변환하여 반환
    }



    /*
    JWT 토큰 만료 여부 확인 (유효성 검증)
     */

    public static Boolean isTokenExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());    //현재시각보다 expiredDate 가 이전인지 확인 (new Date() -> 현재시각)
    }


    /*
    토큰에서 클레임 추출
    :토큰에서 모든 클레임을 추출함(토큰이 유효한지 확인하고 정보를 읽을 때 필요)
     */

    public static Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder() // JWT 해석을 준비하는 단계 (파서 생성)
                .setSigningKey(getKey(key)) // 비밀키를 설정하여 JWT의 신뢰성을 확인
                .build() // 파서를 완성 ~ 파서 사용할 준비 완료시킴
                .parseClaimsJws(token) // JWT 토큰을 해석하여 안에 있는 정보와 유효성 검사
                .getBody(); // JWT의 실제 데이터(Claims)를 추출하여 반환
    }


    /*
    토큰에서 사용자 이름 추출
     */
    public static String getUsernameFromToken(String token, String key) {
        return extractClaims(token, key).get("username", String.class);
    }


    /*
    토큰의 서명 키 변환 (문자열key -> Key 객체로 변환하여 반환)
     */
    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /*
    JWT 토큰 잔여시간 조회
     */
    public static long getRemainingTime(String token, String key) {
        Date expiration = extractClaims(token, key).getExpiration();
        return expiration.getTime() - new Date().getTime();
    }


    public String extractUsername(String token) {
        return "";
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        return null;
    }
}




/*
JWT (JSON Web Token): 인증과 권한을 부여하는 데 사용되는 JSON 기반의 토큰
- 헤더(Header), 페이로드(Payload), 서명(Signature)으로 구성
- 서버는 JWT를 '발급'하고 클라이언트는 요청 시 JWT를 '첨부'하여 자신을 인증함
- Claims: JWT의 Payload에 담길 데이터, 여기서는 username을 클레임에 추가하여 사용자 정보를 저장하고, 이를 통해 인증과 권한 관리함
- 서명 키와 알고리즘: 이 코드에서는 HS256 알고리즘과 키를 사용해 JWT를 서명함
 */
