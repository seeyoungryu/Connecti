package com.seeyoungryu.connecti.fixture;

import com.seeyoungryu.connecti.model.entity.UserEntity;


// fixture -> 테스트용(가짜) userentity 클래스 (mock...?)
public class UserEntityFixture {
    public static UserEntity get(String userName, String password) {

        UserEntity result = new UserEntity();
        result.setId(1L);
        result.setUserName(userName);
        result.setPassword(password);


        return new UserEntity();
    }
}
