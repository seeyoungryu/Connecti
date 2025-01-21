package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@NoArgsConstructor
@Table(name = "posts")
@Entity
@Setter
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")    // @todo: 이 어노테이션이 왜 ~ 소프트 딜리트 기능을 하는지?
public class PostEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter
    private String title;


    @Column(nullable = false, columnDefinition = "TEXT") //타입 변경 ~ 텍스트 타입으로 컬럼이 생성됨
    @Setter
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter
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

    public PostEntity(String title, String body, UserEntity user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public PostEntity(Long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    //post entity 생성 메서드 (기타 필드는 자동으로 만들어짐)
    public static PostEntity of(String title, String body, UserEntity user) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setBody(body);
        postEntity.setUser(user);
        return postEntity;
    }

    @PrePersist
    protected void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

}


