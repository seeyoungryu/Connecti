package com.seeyoungryu.connecti.model;

// DTO 역할을 하는 클래스 ~ 유저 정보를 가져와서 서비스단에서 처리할 때 사용(DB에는 영향이 없음)
// (UserEnity.java -> DB에 저장~)

import com.seeyoungryu.connecti.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

//Todo : implement
@Getter
@AllArgsConstructor
// User.java
public class User {
    private Long id;
    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}

