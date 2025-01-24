package com.seeyoungryu.connecti.fixture;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;

public class PostEntityFixture {
    public static PostEntity get(String userName, Long postId) {
        // UserEntity 생성 및 초기화
        UserEntity user = new UserEntity();
        user.setId(1L); // ID 설정
        user.setUserName(userName); // 유저 이름 설정

        // PostEntity 생성 및 초기화
        PostEntity result = new PostEntity();
        result.setUser(user); // User 설정
        result.setId(postId); // Post ID 동적 설정
        result.setTitle("Test Title"); // Title 초기화 ,Body 초기화 (title과 body 필드를 초기화하여 PostEntity 객체가 불완전하지 않도록 함.
        result.setBody("Test Body");
        return result;
    }
}

// 서비스 레이어에서 사용하는 클래스