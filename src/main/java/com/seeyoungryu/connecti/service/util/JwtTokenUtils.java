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

import com.seeyoungryu.connecti.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenUtils {

    public static String generateAccessToken(String userName, String key, long expiredTimeMs) {
        return doGenerateToken(userName, expiredTimeMs, key);
    }

    public static String generateRefreshToken(String userName, String key, long expiredTimeMs) {
        return doGenerateToken(userName, expiredTimeMs, key);
    }

    private static String doGenerateToken(String userName, long expireTime, String key) {
        Claims claims = Jwts.claims();
        claims.put("username", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Boolean validateToken(String token, UserDetails userDetails, String key) {
        String username = getUsernameFromToken(token, key);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, key);
    }

    private static Boolean isTokenExpired(String token, String key) {
        Date expiration = extractAllClaims(token, key).getExpiration();
        return expiration.before(new Date());
    }

    public static Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsernameFromToken(String token, String key) {
        return extractAllClaims(token, key).get("username", String.class);
    }

    private static Key getSigningKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static long getRemainingTime(String token, String key) {
        Date expiration = extractAllClaims(token, key).getExpiration();
        return expiration.getTime() - new Date().getTime();
    }

    public static String getUsername(String token, String secretKey) {
        return "";
    }

    public static boolean validate(String token, User userDetails, String secretKey) {
        return false;
    }

    public String extractUsername(String token) {
        return "";
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        return null;
    }
}


/*
강사의 JwtTokenUtils 클래스와 내 코드의 차이점을 상세히 살펴보겠습니다. 이 차이는 주로 토큰 생성과 검증 기능을 강화하는 방식에 있습니다.

주요 차이점 및 설명
토큰 유효성 검증 메서드 추가:

내 코드: 토큰을 생성하는 generateToken 메서드만 구현되어 있습니다.
강사 코드: validate, isTokenExpired, getUsername 등 다양한 유효성 검증 메서드가 추가되어 토큰이 만료되었는지, 사용자 정보가 유효한지 확인할 수 있습니다.
설명: validate 메서드는 토큰이 만료되지 않았고, 사용자 정보가 일치하는지 확인하여 보안성을 높입니다.

토큰 발행 시 IssuedAt과 Expiration 설정:

내 코드: setIssuedAt만 설정하고 만료 시간이 없으므로 토큰이 항상 유효합니다.
강사 코드: setExpiration을 설정해 토큰 만료 시간을 지정합니다.
설명: 만료 시간이 없으면 토큰이 무기한 유효하므로 보안성이 떨어질 수 있습니다. setExpiration을 사용하여 만료 기한을 설정하면 토큰의 보안성이 강화됩니다.

두 종류의 토큰 생성 지원:

내 코드: 액세스 토큰만 생성합니다.
강사 코드: generateAccessToken, generateRefreshToken을 통해 액세스 토큰과 리프레시 토큰을 별도로 생성할 수 있습니다.
설명: 리프레시 토큰은 세션 만료 후 새로 로그인하지 않아도 토큰을 갱신할 수 있는 방식으로, 보안을 강화하고 사용자 편의성을 높입니다.

유저네임 키 차이:

내 코드: userName으로 키를 저장합니다.
강사 코드: username으로 키를 저장하여 Spring Security 표준과 일치시킵니다.
남은 유효 시간 반환 기능:

내 코드: 남은 유효 시간을 반환하는 기능이 없습니다.
강사 코드: getRemainMilliSeconds 메서드를 통해 토큰의 남은 유효 시간을 밀리초 단위로 확인할 수 있습니다.
설명: 남은 유효 시간 확인 기능은 사용자 세션 관리 등에 활용할 수 있어 보안성과 관리 효율성을 높입니다.


 */


/*
설명
generateAccessToken과 generateRefreshToken: 두 종류의 토큰을 생성하여 토큰 관리 효율성을 높였습니다.
validateToken: 사용자 정보와 만료 여부를 확인해, 보안성을 높였습니다.
isTokenExpired: 만료 확인 로직을 추가하여 보안성을 강화했습니다.
getRemainingTime: 남은 유효 시간 확인 기능을 추가해 세션 관리에 활용할 수 있습니다.
이로써 더 강력한 토큰 유효성 검증과 세션 관리를 지원하는 JwtTokenUtils 클래스를 만들 수 있습니다.
 */


