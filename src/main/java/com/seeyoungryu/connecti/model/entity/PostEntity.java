package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@NoArgsConstructor
@Table(name = "posts")
@Entity
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")    // @todo: 이 어노테이션이 왜 ~ 소프트 딜리트 되도록?
public class PostEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;


    //외래키 설정
    @ManyToOne(fetch = FetchType.LAZY)  //@Todo 지연로딩 (n+1 확실하게 알기 ~ 블로그)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "registered_at", updatable = false)
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    public PostEntity(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @PrePersist
    protected void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    //post entity 생성 메서드
    public static PostEntity of(String title, String body, UserEntity user) {
        PostEntity postEntity = new PostEntity(title, body);
        postEntity.title = title;
        postEntity.body = body;
        return postEntity;
    }

    public PostEntity(Long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

}
