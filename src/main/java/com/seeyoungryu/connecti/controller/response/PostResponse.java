package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.Post;
import lombok.Getter;

@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String body;

    // 생성자: Post를 기반으로 PostResponse 생성
    public PostResponse(Long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    // 정적 팩토리 메서드
    public static PostResponse fromPost(Post post) {
        // null 값 처리 ~ null 확인 후 안전하게 처리
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }
        return new PostResponse(post.getId(), post.getTitle(), post.getBody());
    }
}
