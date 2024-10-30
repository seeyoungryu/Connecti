package com.seeyoungryu.connecti.model.entity;

import com.seeyoungryu.connecti.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;


// UserEntity - JPA 엔티티 클래스
@Getter
@Table(name = "users")
@Entity
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private String password;

    @Column
    private UserRole role = UserRole.USER;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void onCreate() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    //new entity 만들어주는 메소드 추가
    public static UserEntity of(String userName, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.userName = userName;
        userEntity.password = password;
        return userEntity;
    }
}





/* (어노테이션 정리)
@Entity: JPA 엔티티로 설정하여, Spring Data JPA가 이 클래스를 통해 데이터베이스 테이블과 상호작용하도록 함
@Table(name = "users"): 엔티티가 매핑될 데이터베이스 테이블명을 지정
@SQLDelete: 데이터 삭제 시 실제로는 deleted_at 필드만 업데이트하여 *논리적 삭제를 구현 (소프트 딜리트)
@Where(clause = "deleted_at IS NULL"): deleted_at 필드가 NULL인 데이터만 조회하도록 설정하여, 논리적으로 삭제된 데이터를 제외하고 조회
@PrePersist와 @PreUpdate: 엔티티가 <<처음 저장되거나 업데이트>>될 때 <<자동>>으로 registeredAt과 updatedAt을 설정합니다.
 */