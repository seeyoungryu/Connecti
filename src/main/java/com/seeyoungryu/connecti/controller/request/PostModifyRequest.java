package com.seeyoungryu.connecti.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostModifyRequest {
    public Long postId;
    private String title;
    private String body;

}
