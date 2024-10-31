package com.seeyoungryu.connecti.exception;

import com.seeyoungryu.connecti.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//< 전역 예외 처리를 위한 클래스 - 로그 기록, 응답 반환>
@Slf4j  //로그
@RestControllerAdvice // ->컨트롤러를 통해 들어온 요청의 흐름에서 발생한 예외를 잡아 처리하도록 함
public class GlobalControllerAdvice {

    @ExceptionHandler(ConnectiApplicationException.class)
    //-> 지정된 ConnectiApplicationException 예외가 발생하면 이 메서드를 호출하도록 Spring에 알림 ~ 핸들링(처리) 함
    public ResponseEntity<?> applicationHandler(ConnectiApplicationException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name()));   //ResponseEntity를 통해 HTTP 상태 코드와 오류 메시지를 반환하여 사용자에게 응답

    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> applicationHandler(RuntimeException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //HTTP 응답의 상태 코드를 500 INTERNAL_SERVER_ERROR로 설정 (코드를 설정)
                .body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));   //ResponseEntity를 통해 HTTP 상태 코드와 오류 메시지를 반환 (에러 메세지 설정)

    }
}



/*
GlobalControllerAdvice - 전역 예외 처리 클래스(@RestControllerAdvice)
서비스 계층에서 발생한 예외도 결국 컨트롤러에 전달되기 때문에,
최종적으로 컨트롤러에서 발생한 모든 예외를 이 클래스에서 핸들링함
 */

/*
name() 메서드
: 열거형(enum) 상수의 이름을 문자열로 반환하는 자바 기본 메서드
name()을 사용하면 열거형 상수 이름을 문자열로 가져올 수 있어, 응답 메시지에서 에러의 종류를 명확히 전달할 수 있음
 */

