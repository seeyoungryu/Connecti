package com.seeyoungryu.connecti.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DUPLICATE_USER_NAME,
    ;

    private HttpStatus status;
    
}
