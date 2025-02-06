package com.seeyoungryu.connecti.exception;

import com.seeyoungryu.connecti.controller.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ConnectiApplicationException.class)
    public ResponseEntity<?> applicationHandler(ConnectiApplicationException e) {
        log.error("Error occurs {}", e.toString());

        // 권한 관련 에러일 경우 명시적으로 403 반환
        if (e.getErrorCode() == ErrorCode.INVALID_PERMISSION) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getErrorCode().name(), e.getMessage()));
        }

        // 그 외의 경우 ErrorCode에 정의된 상태 코드 반환
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error occurs {}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred."));
    }
}
