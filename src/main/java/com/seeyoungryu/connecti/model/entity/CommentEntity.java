package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

//    public CommentEntity(String content, PostEntity post, UserEntity user) {
//        this.content = content;
//        this.post = post;
//        this.user = user;
//    }

    // ID 포함한 생성자 추가 (테스트용)
    public CommentEntity(Long id, String content, PostEntity post, UserEntity user) {
        this.id = id;  //하드코딩
        this.content = content;
        this.post = post;
        this.user = user;
    }
}
