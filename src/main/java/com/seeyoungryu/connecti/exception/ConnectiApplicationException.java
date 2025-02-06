package com.seeyoungryu.connecti.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


//예외 처리 클래스(전체 앱의 예외 포괄적 처리)
@Getter
@AllArgsConstructor
public class ConnectiApplicationException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;


    //에러코드만 반환
    public ConnectiApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }


    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        }
        return String.format("%s, %s", errorCode.name(), message);
    }


}

