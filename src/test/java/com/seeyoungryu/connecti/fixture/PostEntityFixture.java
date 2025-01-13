package com.seeyoungryu.connecti.fixture;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;

public class PostEntityFixture {
    public static PostEntity get(String userName, Long postId) {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(1L);
        return result;
    }
}

// 서비스 레이어에서 사용하는 클래스