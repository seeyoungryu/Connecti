package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import lombok.Getter;

@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String body;

    // 생성자: PostEntity를 기반으로 PostResponse 생성
    public PostResponse(PostEntity postEntity) {
        this.id = postEntity.getId();
        this.title = postEntity.getTitle();
        this.body = postEntity.getBody();
    }

    // 정적 팩토리 메서드
    public static PostResponse fromPost(PostEntity postEntity) {
        // null 값 처리 ~ null 확인 후 안전하게 처리
        if (postEntity == null) {
            throw new IllegalArgumentException("PostEntity cannot be null");
        }
        return new PostResponse(postEntity);
    }
}
