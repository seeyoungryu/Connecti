package com.seeyoungryu.connecti.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


//ENUM(열거형) 클래스
//특정 오류를 코드화하고 명확하게 표현함
@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATE_USER_NAME(HttpStatus.CONFLICT, "user name is duplicated"),
    ;

    private HttpStatus status; //Spring의 HTTP 상태 코드를 통해,어떤 종류의 에러가 발생했는지 REST API 응답에서 확인할 수 있음
    private String message;

}


/*
private HttpStatus status;
-> Spring의 HTTP 상태 코드를 통해,어떤 종류의 에러가 발생했는지 REST API 응답에서 확인할 수 있음
(HttpStatus.CONFLICT - 중복 에러나 자원 충돌이 발생했을 때 사용)
사용자가 중복된 이름으로 회원가입을 시도할 때 DUPLICATE_USER_NAME 상수가 선택되어 ~ 해당 에러 코드와 메시지를 포함한 응답을 전송
 */
