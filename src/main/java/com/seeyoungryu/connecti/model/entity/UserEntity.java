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
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    /*
    권장사항: @Enumerated(EnumType.STRING)을 사용하여 문자열로 저장하면 역할(Role)을 직관적으로 확인할 수 있습니다. 따라서 강사 코드처럼 @Enumerated 설정을 추가하는 것이 좋습니다.
     */

    /*
    todo : 유저롤에 컬럼 어노테이션 안붙여도 되나?
     */


    @Setter
    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    //new entity 만들어주는 메소드 추가
    public static UserEntity of(String userName, String encodedPwd) {
        UserEntity userEntity = new UserEntity();
        userEntity.userName = userName;
        userEntity.password = encodedPwd;
        return userEntity;
    }
}

/*
todo userentity를 만들려고 하는 코드 안에 왜 또 userEntity.setUserName(userName); 이런 코드가 들어가는지..? 이러면 계속 서로를 참조하게 되는거 아닌가?
 */





/* (어노테이션 정리)
@Entity: JPA 엔티티로 설정하여, Spring Data JPA가 이 클래스를 통해 데이터베이스 테이블과 상호작용하도록 함
@Table(name = "users"): 엔티티가 매핑될 데이터베이스 테이블명을 지정
@SQLDelete: 데이터 삭제 시 실제로는 deleted_at 필드만 업데이트하여 *논리적 삭제를 구현 (소프트 딜리트)
@Where(clause = "deleted_at IS NULL"): deleted_at 필드가 NULL인 데이터만 조회하도록 설정하여, 논리적으로 삭제된 데이터를 제외하고 조회
@PrePersist와 @PreUpdate: 엔티티가 <<처음 저장되거나 업데이트>>될 때 <<자동>>으로 registeredAt과 updatedAt을 설정합니다.
 */