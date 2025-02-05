package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class PostResponse {
    private final Long id;
    private final String title;
    private final String body;


//    public static PostResponse fromPost(Post post) {
//        // null 값 처리 ~ null 확인 후 안전하게 처리
//        if (post == null) {
//            throw new IllegalArgumentException("Post cannot be null");
//        }
//        return new PostResponse(post.getId(), post.getTitle(), post.getBody());
//    }

    public static PostResponse fromPost(Post post) {   //PostResponse 객체 생성
        return Optional.ofNullable(post)   //Optional을 활용한 null 처리
                .map(p -> new PostResponse(p.getId(), p.getTitle(), p.getBody()))
                .orElseThrow(() -> new IllegalArgumentException("Post cannot be null"));
    }
}
