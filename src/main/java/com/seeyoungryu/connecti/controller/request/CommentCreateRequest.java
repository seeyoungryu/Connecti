package com.seeyoungryu.connecti.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    private String content;

    public CommentCreateRequest(String content) {
        this.content = content;
    }
}
