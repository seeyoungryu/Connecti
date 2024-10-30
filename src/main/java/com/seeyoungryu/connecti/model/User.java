package com.seeyoungryu.connecti.model;

import com.seeyoungryu.connecti.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

/*
User.java - 애플리케이션 내 사용자 *모델* 클래스
역할: UserEntity와 다르게, 비즈니스 로직(서비스 레이어)에서 활용되는 데이터 모델임
데이터베이스와 연결되지 않으며(디비에 영향 없음), UserEntity의 데이터를 기반으로 사용자 정보를 관리(유저 정보를 가져와서 서비스단에서 처리할 때 사용)
(UserEnity.java -> DB에 저장~)
 */

@Getter
@AllArgsConstructor

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

/* fromEntity : 정적 팩토리 메서드 (new 키워드 사용x)
- UserEntity 객체를 받아 그 정보만으 User 객체를 생성하고 반환(new User) -> 엔티티를 서비스 계층에서 활용할 수 있게 함
*/



