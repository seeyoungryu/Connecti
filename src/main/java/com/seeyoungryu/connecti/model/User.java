package com.seeyoungryu.connecti.model;

import com.seeyoungryu.connecti.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/*
User.java - 애플리케이션 내 사용자 *모델* 클래스
역할: UserEntity와 다르게, 비즈니스 로직(서비스 레이어)에서 활용되는 데이터 모델임
데이터베이스와 연결되지 않으며(디비에 영향 없음), UserEntity의 데이터를 기반으로 사용자 정보를 관리(유저 정보를 가져와서 서비스단에서 처리할 때 사용)
(UserEnity.java -> DB에 저장~)
 */

@Getter
@AllArgsConstructor
public class User implements UserDetails {

    private Long id;
    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static User fromEntity(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getUserName(),
                userEntity.getPassword(),
                userEntity.getRole(),
                userEntity.getRegisteredAt(),
                userEntity.getUpdatedAt(),
                userEntity.getDeletedAt()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return deletedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return deletedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return deletedAt == null;
    }

    @Override
    public boolean isEnabled() {
        return deletedAt == null;
    }
}


/* fromEntity : 정적 팩토리 메서드 (new 키워드 사용x)
- UserEntity 객체를 받아 그 정보만으 User 객체를 생성하고 반환(new User) -> 엔티티를 서비스 계층에서 활용할 수 있게 함
*/

/*

UserRole 필드와 권한 관리

내 코드: UserRole 필드는 있지만 권한 관리는 하지 않습니다.
강사 코드: getAuthorities 메서드를 오버라이드하여 UserRole에 따라 권한(GrantedAuthority)을 설정합니다.
권장사항: 권한 기반의 인증이 필요하다면 강사 코드처럼 getAuthorities 메서드를 통해 권한을 설정하는 것이 유용합니다.

계정 상태 체크 메서드

내 코드: 계정 만료, 잠금, 비밀번호 만료, 활성화 상태 체크 메서드가 없습니다.
강사 코드: isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled 메서드를 통해 계정의 유효성을 체크합니다. removedAt 필드를 사용하여 삭제된 계정을 비활성화합니다.
권장사항: Spring Security와 통합이 필요한 경우 강사 코드처럼 UserDetails 인터페이스를 구현하고 상태 체크 메서드를 정의하는 것이 좋습니다.


 */


/*
설명
UserDetails 구현: Spring Security에서 사용자 정보를 활용할 수 있도록 UserDetails를 구현했습니다.
getAuthorities: 역할을 권한으로 변환하여 Spring Security가 인식할 수 있게 했습니다.
계정 상태 메서드: deletedAt 필드를 기준으로 계정의 활성 상태를 판단하도록 설정했습니다.
이렇게 설정하면, User 클래스는 Spring Security와 통합되어 인증, 권한 관리에 활용될 수 있으며, 추가적인 계정 상태 관리도 가능해집니다.
 */


