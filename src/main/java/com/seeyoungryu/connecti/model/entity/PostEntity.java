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
@Where(clause = "deleted_at IS NULL")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

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
    protected void onCreate() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    //post entity 생성 메서드
    public static PostEntity of(String title, String body) {
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
