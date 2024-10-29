package com.seeyoungryu.connecti.exception;

import com.seeyoungryu.connecti.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j  //로그 확인
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ConnectiApplicationException.class)
    public ResponseEntity<?> applicationHandler(ConnectiApplicationException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));
    }
}
