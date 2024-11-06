package com.seeyoungryu.connecti.exception;

import com.seeyoungryu.connecti.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//
////< 전역 예외 처리를 위한 클래스 - 로그 기록, 응답 반환>
//@Slf4j  //로그
//@RestControllerAdvice // ->컨트롤러를 통해 들어온 요청의 흐름에서 발생한 예외를 잡아 처리하도록 함
//public class GlobalControllerAdvice {
//
//    @ExceptionHandler(ConnectiApplicationException.class)
//    //-> 지정된 ConnectiApplicationException 예외가 발생하면 이 메서드를 호출하도록 Spring에 알림 ~ 핸들링(처리) 함
//    public ResponseEntity<?> applicationHandler(ConnectiApplicationException e) {
//        log.error("Error occurs {}", e.toString());
//        return ResponseEntity.status(e.getErrorCode().getStatus())
//                .body(Response.error(e.getErrorCode().name()));   //ResponseEntity를 통해 HTTP 상태 코드와 오류 메시지를 반환하여 사용자에게 응답
//
//    }
//
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<?> applicationHandler(RuntimeException e) {
//        log.error("Error occurs {}", e.toString());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //HTTP 응답의 상태 코드를 500 INTERNAL_SERVER_ERROR로 설정 (코드를 설정)
//                .body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));   //ResponseEntity를 통해 HTTP 상태 코드와 오류 메시지를 반환 (에러 메세지 설정)
//
//    }
//}
//
//
//
/// *
//GlobalControllerAdvice - 전역 예외 처리 클래스(@RestControllerAdvice)
//서비스 계층에서 발생한 예외도 결국 컨트롤러에 전달되기 때문에,
//최종적으로 컨트롤러에서 발생한 모든 예외를 이 클래스에서 핸들링함
// */
//

///*
//name() 메서드
//: 열거형(enum) 상수의 이름을 문자열로 반환하는 자바 기본 메서드
//name()을 사용하면 열거형 상수 이름을 문자열로 가져올 수 있어, 응답 메시지에서 에러의 종류를 명확히 전달할 수 있음
// */
//

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ConnectiApplicationException.class)
    //-> 지정된 ConnectiApplicationException 예외가 발생하면 이 메서드를 호출하도록 Spring에 알림 ~ 핸들링(처리) 함
    public ResponseEntity<?> applicationHandler(ConnectiApplicationException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name(), e.getMessage()));    //ResponseEntity를 통해 HTTP 상태 코드와 오류 메시지를 반환하여 사용자에게 응답
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error occurs {}", e.toString());  // 예상치 못한 예외에 대한 로그 기록
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred."));
    }
}


/*
강사 코드에서는 전역 예외 처리 기능이 일부 간소화되어 있지만, 그 외에 필요한 기능은 대부분 포함되어 있습니다. 여기서 두 코드의 차이점과 추가할 수 있는 개선사항을 정리하겠습니다.

차이점 분석
예외 로깅 기능

내 코드: @Slf4j를 사용해 예외가 발생했을 때 로그를 기록합니다. 예외 로그를 남김으로써 서버에서 발생한 문제를 추적할 수 있습니다.
강사 코드: 예외 발생 시 로그를 기록하는 부분이 없습니다. 따라서 예외가 발생했을 때 콘솔이나 로그 파일에 기록되지 않습니다.
개선점: 강사 코드에 @Slf4j를 추가하고, 예외를 로그로 기록하도록 log.error("Error occurs {}", e.toString()); 구문을 추가하면, 예외 발생 시 로그를 남겨서 문제를 파악하는 데 도움이 됩니다.
예외 응답 메시지 형식

내 코드: Response.error(e.getErrorCode().name())를 사용해 에러 코드를 반환합니다.
강사 코드: Response.error(e.getErrorCode().name(), e.getMessage())를 사용하여 에러 코드와 에러 메시지를 같이 반환합니다. 이는 사용자에게 더 상세한 에러 정보를 제공할 수 있습니다.
개선점: 내 코드에도 에러 메시지(e.getMessage())를 포함하여 Response.error(e.getErrorCode().name(), e.getMessage()) 형식으로 반환하면 사용자에게 더 상세한 정보를 제공할 수 있습니다.
RuntimeException 예외 처리

내 코드: RuntimeException에 대한 별도의 ExceptionHandler를 정의하여, 예상치 못한 런타임 예외가 발생했을 때도 500 오류를 반환하도록 처리합니다.
강사 코드: RuntimeException에 대한 별도의 예외 처리가 없습니다. 따라서 SimpleSnsApplicationException이 아닌 다른 예외가 발생하면 기본 예외 처리로 전달될 수 있습니다.
개선점: 강사 코드에도 RuntimeException을 처리하는 ExceptionHandler를 추가하면, 예상치 못한 오류가 발생했을 때도 일관된 에러 응답을 반환할 수 있습니다.
개선된 강사 코드 제안
강사 코드에 예외 로깅, RuntimeException 처리, 그리고 에러 메시지 반환 형식을 개선하여 제안할 수 있습니다.
 */


/*
요약
예외 로그 기록: @Slf4j를 추가하고 log.error를 통해 예외를 로그로 기록하면, 서버에서 발생한 문제를 추적하는 데 도움이 됩니다.
에러 메시지 반환 형식: 응답에서 에러 코드와 함께 에러 메시지를 반환하여, 사용자에게 더 명확한 에러 정보를 제공할 수 있습니다.
RuntimeException 처리 추가: RuntimeException에 대한 핸들러를 추가하여 예상치 못한 예외 발생 시에도 일관된 응답을 반환할 수 있습니다.
이렇게 개선된 코드를 사용하면 강사 코드에서도 더욱 견고한 예외 처리를 할 수 있습니다.
 */
