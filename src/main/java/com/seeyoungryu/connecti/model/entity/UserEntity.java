package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.sql.Timestamp;
import java.time.Instant;

@Setter
@Getter
@Table
@Entity
@SQLDelete(sql = "UPDATED user SET deleted_at=NOW() where id=? ")
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    public String userName;
    @Column
    public String password;

    //메타데이터.. 디버깅에 사용할 수 있음
    @Column
    private Timestamp registeredAt; //유저 등록 시간 저장
    @Column
    private Timestamp updatedAt; //유저 등록 시간 저장
    @Column
    private Timestamp deletedAt;  //유저 삭제 시간 저장 ~ 로그를 찾아볼 수 있음 -> *소프트 딜리트 형태 (삭제된 데이터를 나중에 복구하거나, 삭제 이력을 확인할 수 있다는 점이다. 또한, 삭제된 데이터가 필요하지 않은 조회에서 제외되도록 할 수 있다.)

    @PrePersist
    void registeredAt() {
//        this.registeredAt = new Timestamp(System.currentTimeMillis());
        this.registeredAt = Timestamp.from(Instant.now());

    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }


}
