package com.seeyoungryu.connecti.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

//DTO(데이터 전송 객체) 클래스
@AllArgsConstructor
@Getter
public class UserLoginRequest {

    private String userName;
    private String password;


}
