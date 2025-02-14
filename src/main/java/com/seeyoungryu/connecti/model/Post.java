package com.seeyoungryu.connecti.model;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@RequiredArgsConstructor
public class Post { //DTO 클래스 (PostEntity의 데이터를 기반으로 새로운 Post 객체를 생성하여 반환)


    private final Long id;
    private final String title;
    private final String body;
    private final LocalDateTime registeredAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    public static Post fromEntity(PostEntity postEntity) {
        if (postEntity == null) {
            log.error("Post.fromEntity() received null PostEntity!");
            throw new IllegalArgumentException("PostEntity cannot be null");
        }

        return new Post(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getBody(),
                postEntity.getRegisteredAt(),
                postEntity.getUpdatedAt(),
                postEntity.getDeletedAt() != null ? postEntity.getDeletedAt() : null //null 체크 추가
        );
    }

}
