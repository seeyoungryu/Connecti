package com.seeyoungryu.connecti.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private String resultCode;
    private T result;

    public static Response<Void> error(String errorCord) {
        return new Response<>(errorCord, null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", null);  //하드코딩
    }
}
