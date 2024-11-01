package com.seeyoungryu.connecti.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

//도구(유틸리티) 클래스

public class JwtTokenUtils {
    public static String generateToken(String userName, String key, Long expiredTimeMs) {

        Claims claims = Jwts.claims();
        claims.put("userName", userName);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getKey(key), SignatureAlgorithm.HS256)  //해시 알고리즘 사용
                .compact();
    }

    private static Key getKey(String key) {   //키 반환 메서드 (문자열로 된 키를 JWT 서명에 사용할 수 있는 Key 객체로 변환)
        byte[] keyByte = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyByte);
    }
}



/*  << claim >>
    : JWT의 < Payload > 를 구성하는 데이터 -> JWT의에 들어갈 < 사용자 정보 > 등을 담는 역할을 함  ( *JWT 구성 : Header.Payload.Signature )
       - generateToken() 매개변수 : 유저네임 -> claim에 추가되어 Payload에 포함됨 -> 사용자 식별에 사용됨
        ,key(토큰 서명을 위한 암호화 키) → Signature 부분을 생성하는 데 활용
        ,expiredTimeMs -> 토큰 만료 시간(밀리 초)을 설정하는 데 사용되며, Payload의 exp 클레임에 들어감  */
