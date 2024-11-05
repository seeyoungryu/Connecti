package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import lombok.Getter;

@Getter
public class PostResponse {
    private final Long id;
    private final String title;
    private final String body;

    public PostResponse(PostEntity postEntity) {
        this.id = postEntity.getId();
        this.title = postEntity.getTitle();
        this.body = postEntity.getBody();
    }
}
