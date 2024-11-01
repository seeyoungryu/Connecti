package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@NoArgsConstructor
@Getter
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
    private String content;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    public PostEntity(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static PostEntity of(String title, String content) {
        return new PostEntity(title, content);
    }
}
