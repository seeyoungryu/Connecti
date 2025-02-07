package com.seeyoungryu.connecti.fixture;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;


// *Fixture -> 서비스 레이어에서 사용하는 클래스
public class PostEntityFixture {
    public static PostEntity get(String userName, Long postId) {
        // UserEntity 생성, 초기화
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUserName(userName);

        // PostEntity 생성, 초기화
        PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(postId);
        result.setTitle("Test Title");
        result.setBody("Test Body");
        return result;
    }
}

