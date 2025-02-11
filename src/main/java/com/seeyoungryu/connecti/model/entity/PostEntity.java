package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Table(name = "posts")
@Entity // JPA 관리 객체 클래스
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?") // Soft Delete
@Where(clause = "deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 활성화
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ** MySQL에서는 기본적으로 IDENTITY가 적절하지만, 대량 트랜잭션에서는 SEQUENCE가 더 나을 수 있음 **
    // (향후 PostgreSQL로 이전하거나 성능 최적화가 필요하면 SEQUENCE 고려 가능)

    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @CreatedDate
    @Column(name = "registered_at", updatable = false)
    private LocalDateTime registeredAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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

    public boolean likePost(UserEntity user) {
        return this.likedUsers.add(user); // 추가 성공 여부 반환
    }

    public boolean unlikePost(UserEntity user) {
        return this.likedUsers.remove(user); // 삭제 성공 여부 반환
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateBody(String body) {
        this.body = body;
    }
}
