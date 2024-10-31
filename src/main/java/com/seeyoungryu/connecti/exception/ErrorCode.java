package com.seeyoungryu.connecti.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


//ENUM(열거형) 클래스
//특정 오류를 코드화하고 명확하게 표현함
@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATE_USER_NAME(HttpStatus.CONFLICT, "User name is duplicated"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password"),
    ;


    private HttpStatus status; // Spring의 HTTP 상태 코드를 통해 어떤 종류의 에러가 발생했는지 REST API 응답에서 확인할 수 있음
    private String message;
}


