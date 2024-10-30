package com.seeyoungryu.connecti.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


//응답의 기본 구조를 캡슐화하는 클래스
@Getter
@AllArgsConstructor
public class Response<T> {
    private String resultCode;
    private T result;

    public static Response<Void> error(String errorCord) {
        return new Response<>(errorCord, null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }
}

