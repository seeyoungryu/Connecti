package com.seeyoungryu.connecti.model.entity;

import com.seeyoungryu.connecti.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;


// UserEntity - JPA 엔티티 클래스
@NoArgsConstructor
@Getter
@Table(name = "users")
@Entity
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class UserEntity {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column
    private String userName;

    @Setter
    @Column
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)   //@Enumerated 설정 : UserRole 열거형을 데이터베이스에 문자열로 저장하겠다는 설정 -> 역할(Role)을 직관적으로 확인
    private UserRole role = UserRole.USER;
    //@Column을 추가하지 않더라도 role 필드는 데이터베이스 컬럼으로 자동으로 매핑됨

    @Setter
    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    //of ~ *객체 생성(엔티티 자체)을 위해 사용
    //서비스 계층 -> 데이터베이스: 엔티티 객체를 생성해 데이터베이스에 저장하는 흐름을 위한 메서드
    public static UserEntity of(String userName, String encodedPwd) {
        UserEntity userEntity = new UserEntity();
        userEntity.userName = userName;
        userEntity.password = encodedPwd;
        return userEntity;
    }

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }
}


/*
@SQLDelete: 데이터 삭제 시 실제로는 deleted_at 필드만 업데이트하여 *논리적 삭제를 구현 (소프트 딜리트)
@Where(clause = "deleted_at IS NULL"): deleted_at 필드가 NULL인 데이터만 조회하도록 설정하여, 논리적으로 삭제된 데이터를 제외하고 조회
@PrePersist와 @PreUpdate: 엔티티가 <<처음 저장되거나 업데이트>>될 때 <<자동>>으로 registeredAt과 updatedAt을 설정합니다.
 */