package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "alarms")
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 활성화
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 유저와 연관
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY) // 게시글과 연관
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private AlarmEntity(UserEntity user, PostEntity post) {
        this.user = user;
        this.post = post;
    }

    public static AlarmEntity of(UserEntity user, PostEntity post) {
        return new AlarmEntity(user, post);
    }
}



/*
유저(UserEntity)와 게시글(PostEntity)을 연결
생성 시간(createdAt)을 자동 저장 (JPA Auditing)
of() 메서드로 객체 생성 편리화
 */