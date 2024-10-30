package com.seeyoungryu.connecti.fixture;

import com.seeyoungryu.connecti.model.entity.UserEntity;


/* < Fixture 클래스 사용 >
테스트에서 사용될 더미(가짜) UserEntity 데이터를 생성하여,
 반복적인 코드 작성을 줄이고 테스트의 가독성을 높이기 위함
 */


public class UserEntityFixture {
    public static UserEntity get(String userName, String password) {

        UserEntity result = new UserEntity();
        result.setId(1L);
        result.setUserName(userName);
        result.setPassword(password);


        return new UserEntity();
    }
}

