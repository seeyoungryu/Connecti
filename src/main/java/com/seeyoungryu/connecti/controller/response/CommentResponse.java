package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String userName;
    private String content;

    // CommentEntity -> CommentResponse 변환 메서드
    public static CommentResponse fromEntity(CommentEntity comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser().getUserName(),
                comment.getContent()
        );
    }
}
