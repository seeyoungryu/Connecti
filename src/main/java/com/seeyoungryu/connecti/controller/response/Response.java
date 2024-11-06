package com.seeyoungryu.connecti.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


////응답의 기본 구조를 캡슐화하는 클래스
//@Getter
//@AllArgsConstructor
//public class Response<T> {
//    private String resultCode;
//    private T result;
//
//    public static Response<Void> error(String errorCord) {
//        return new Response<>(errorCord, null);
//    }
//
//    public static <T> Response<T> success(T result) {
//        return new Response<>("SUCCESS", result);
//    }
//}
//

@Getter
@AllArgsConstructor
public class Response<T> {
    private String resultCode;
    private String resultMessage;
    private T result;

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", null, result);
    }

    public static Response<Void> error(String resultCode, String resultMessage) {
        return new Response<>(resultCode, resultMessage, null);
    }

    // 메시지가 필요 없는 단순 오류 응답을 위한 메서드
    public static Response<Void> error(String resultCode) {
        return new Response<>(resultCode, null, null);
    }
}

/*
두 Response 클래스는 공통적으로 API 응답의 일관된 형식을 제공하기 위해 만들어졌지만, 구성 요소와 오류 처리 방식에서 차이가 있습니다. 아래에서 두 클래스를 비교하고, 코드가 어떤 상황에 적합한지 설명하겠습니다.

코드 비교
Response 클래스 (내 코드)

필드:
resultCode: 응답의 성공 또는 오류 상태를 나타냄.
result: 실제 데이터나 메시지를 담는 필드.
메서드:
error(String errorCode): errorCode만 전달받아 오류 응답을 생성.
success(T result): 성공 시 응답 데이터를 포함하여 응답을 생성.
특징:
단순화된 오류 처리 방식을 사용하여, 에러 코드만으로 에러 응답을 생성함.
resultMessage 필드가 없어서 에러 시 구체적인 메시지를 전달하는 기능이 없음.
Response 클래스 (강사 코드)

필드:
resultCode: 응답의 성공 또는 오류 상태를 나타냄.
resultMessage: 오류 메시지나 추가 정보를 담기 위한 필드.
result: 실제 데이터나 메시지를 담는 필드.
메서드:
error(String resultCode, String resultMessage): 에러 코드와 구체적인 에러 메시지를 전달받아 오류 응답을 생성.
success(T result): 성공 시 응답 데이터를 포함하여 응답을 생성.
특징:
에러 메시지를 포함할 수 있어서, 클라이언트에 구체적인 오류 정보를 제공할 수 있음.
resultMessage 필드가 있어, 오류가 발생했을 때 사용자에게 더 명확한 정보를 전달 가능.
차이점 정리
항목	내 코드	강사 코드
오류 응답 형식	errorCode만 포함	errorCode와 resultMessage 모두 포함
성공 응답 형식	SUCCESS와 result 포함	SUCCESS와 result 포함
오류 메시지 전달	지원하지 않음	resultMessage 필드를 통해 오류 메시지 전달 가능
단순성	필드가 적고 단순함	필드가 많고 더 구체적인 정보를 제공할 수 있음
어떤 상황에서 사용해야 할까?
내 코드:

단순한 API 응답 구조를 원할 때 적합합니다.
에러 메시지 없이 에러 코드만으로 충분히 응답을 처리할 수 있는 상황에서 사용하기 좋습니다.
강사 코드:

상세한 오류 메시지를 전달해야 하는 경우에 적합합니다.
클라이언트가 에러 코드를 기반으로 추가적인 오류 정보를 받을 수 있도록 할 때 유용합니다. 예를 들어, 사용자에게 오류 이유를 좀 더 구체적으로 안내할 필요가 있을 때 유리합니다.
결론 및 제안
유연성 및 확장성을 고려할 때는 강사 코드처럼 resultMessage를 포함하여 오류에 대한 추가 정보를 제공하는 것이 좋습니다.
현재 프로젝트가 간단한 구조로 유지될 예정이라면, 내 코드를 사용해도 충분합니다. 하지만 확장성을 고려한다면 강사 코드와 같이 resultMessage를 추가하는 것이 나중에 유용할 수 있습니다.
예시 코드 통합
두 코드를 합쳐서 더 유연한 형태로 구현할 수 있습니다:

이렇게 하면:

success 메서드로 성공 응답을 만들 수 있고,
error 메서드를 두 가지 형태로 오버로드하여, 오류 메시지 유무에 따라 오류 응답을 생성할 수 있습니다.
profile

 */

