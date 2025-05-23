package com.seeyoungryu.connecti.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private String resultCode;
    private String resultMessage;
    private T result;


    /*
    success 메서드 ~ 성공 응답
     */
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>("SUCCESS", null, result);
    }


    /*
    error 메서드를 두 가지 형태로 오버로드하여, 오류 메시지 유무에 따라 오류 응답을 생성
     */
    public static ApiResponse<Void> error(String resultCode, String resultMessage) {
        return new ApiResponse<>(resultCode, resultMessage, null);      // resultMessage -> 오류에 대한 추가 정보를 제공
    }

    // 메시지가 필요 없는 단순 오류 응답을 위한 메서드
    public static ApiResponse<Void> error(String resultCode) {
        return new ApiResponse<>(resultCode, null, null);
    }
}
