package com.seeyoungryu.connecti.model;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

    /*
Post.java - 애플리케이션 내 게시글 데이터 *모델* 클래스
데이터베이스와 연결되지 않으며(디비에 영향 없음), PostEntity 데이터를 기반으로 게시물 정보를 관리(게시물 정보를 가져와서 서비스단에서 처리할 때 사용)
 */


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
                postEntity.getBody(),
                postEntity.getTitle(),
                postEntity.getRegisteredAt(),
                postEntity.getUpdatedAt(),
                postEntity.getDeletedAt()
        );

    }

}
