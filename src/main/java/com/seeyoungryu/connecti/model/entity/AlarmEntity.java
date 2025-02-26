package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "alarms")
@SQLDelete(sql = "UPDATE alarms SET deleted_at = NOW() WHERE id = ?")  // 삭제 요청 시 deleted_at 업데이트
@Where(clause = "deleted_at IS NULL")  // deleted_at이 NULL인 데이터만 조회되도록 설정
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;  // Soft Delete를 위한 필드 추가

    //알람 읽음 처리
    // isRead 필드를 추가하여 알람이 읽혔는지를 관리할 수 있음.
    //markAsRead() 메서드를 추가하여 알람을 읽음 처리 가능.

    private boolean isRead = false;  // 기본값 false (안 읽음)

    public static AlarmEntity of(UserEntity user, PostEntity post) {
        AlarmEntity alarm = new AlarmEntity();
        alarm.user = user;
        alarm.post = post;
        alarm.createdAt = LocalDateTime.now();
        return alarm;
    }

    public void markAlarmAsRead() {
        this.isRead = true;
    }
}

/*
유저(UserEntity)와 게시글(PostEntity)을 연결
생성 시간(createdAt)을 자동 저장 (JPA Auditing)
of() 메서드로 객체 생성 편리화
 */


/*
deletedAt 필드를 추가하여 실제 삭제가 아닌 논리적 삭제를 적용.
@SQLDelete(sql = "UPDATE alarms SET deleted_at = NOW() WHERE id = ?")
deleteById() 실행 시, 실제 삭제가 아닌 deleted_at 값을 업데이트함.
@Where(clause = "deleted_at IS NULL")
삭제된 알람(즉, deleted_at 값이 있는 알람)은 조회되지 않도록 설정.

적용 후 동작
: 사용자가 알람을 삭제하면 deleted_at이 현재 시간으로 설정됨.
이후 조회할 때 deleted_at이 NULL인 알람만 반환되므로, 삭제된 알람은 조회되지 않음.

 */