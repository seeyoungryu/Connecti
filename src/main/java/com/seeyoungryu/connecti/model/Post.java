package com.seeyoungryu.connecti.model;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Post { //DTO 클래스 (데이터 가공 후 postEntity 반환 (postEntity로 변환)

    private final Long id;
    private final String title;
    private final String body;
    private final LocalDateTime registeredAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    public static Post fromEntity(PostEntity postEntity) {
        return new Post(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getBody(),
                postEntity.getRegisteredAt().toLocalDateTime(),
                postEntity.getUpdatedAt().toLocalDateTime(),
                postEntity.getDeletedAt() != null ? postEntity.getDeletedAt().toLocalDateTime() : null //null 체크 추가
        );
    }
}
