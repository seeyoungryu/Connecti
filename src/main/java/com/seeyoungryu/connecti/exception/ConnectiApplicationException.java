package com.seeyoungryu.connecti.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


//예외 처리 클래스(본 앱~ 전반에서 발생하는 예외를 포괄적으로 처리함)
@Getter
@AllArgsConstructor
public class ConnectiApplicationException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        }
        return String.format("%s, %s", errorCode.name(), message);
    }

}

//@AllArgsConstructor : 모든 필드를 포함한 생성자를 생성 ~ 예외 생성 시 초기화할 수 있게 함
