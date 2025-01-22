package com.seeyoungryu.connecti.model;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Post {

    private Long id;
    private String title;
    private String body;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Post fromEntity(PostEntity postEntity) {
        return new Post(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getBody(),
                postEntity.getRegisteredAt(),
                postEntity.getUpdatedAt(),
                postEntity.getDeletedAt()
        );
    }
}
