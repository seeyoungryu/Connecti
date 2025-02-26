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
import java.util.List;


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


    @OneToMany(mappedBy = "user")
    private List<PostEntity> posts;
    @Setter
    @Enumerated(EnumType.STRING)   //UserRole 열거형을 데이터베이스에 문자열로 저장하겠다는 설정
    private UserRole role = UserRole.USER;
    @Setter
    @Column(name = "registered_at")
    private Timestamp registeredAt;
    //@Column을 추가하지 않더라도 role 필드는 데이터베이스 컬럼으로 자동으로 매핑됨
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlarmEntity> alarms;

    /*
    알람(AlarmEntity)과 사용자(UserEntity) 간의 관계를 설정하여, 한 사용자가 여러 개의 알람을 가질 수 있도록 함.
    cascade = CascadeType.ALL → 유저가 삭제되면 알람도 함께 삭제됨.
    orphanRemoval = true → 특정 유저에서 알람이 제거되면 DB에서도 삭제됨.
     */


    // comment ~ 생성자 추가
    public UserEntity(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

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

