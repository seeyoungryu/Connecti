package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "registered_at", updatable = false)
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    // add : 좋아요한 사용자 목록 (ManyToMany 관계 설정)
    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> likedUsers = new HashSet<>();

    public PostEntity(String title, String body, UserEntity user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public static PostEntity of(String title, String body, UserEntity user) {
        return new PostEntity(title, body, user);
    }

    @PrePersist
    protected void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    //게시물 좋아요 기능
    public void likePost(UserEntity user) {
        this.likedUsers.add(user); // 좋아요한 사용자 추가
    }

    //게시물 좋아요 취소 기능
    public void unlikePost(UserEntity user) {
        this.likedUsers.remove(user); // 좋아요한 사용자 제거
    }
}
