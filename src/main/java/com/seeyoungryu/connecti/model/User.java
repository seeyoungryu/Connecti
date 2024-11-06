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


// 참고: UserEntity -> 데이터베이스 담당(DB 직접연결) , User -> 서비스 레이어 담당(서비스에서 사용할 정보를 담음)


@Getter
@AllArgsConstructor
public class User implements UserDetails {  //UserDetails 구현: Spring Security에서 사용자 정보를 활용할 수 있도록 UserDetails를 구현

    private Long id;
    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    // 정적 팩토리 메서드 (사용 -> User.fromEntity)
    // * 변환 - UserEntity -> User
    // 데이터베이스 -> 서비스 계층: 엔티티의 데이터를 서비스 레이어에서 사용할 객체에 넣는 작업(서비스 레이어에서 사용하기 위한 데이터 모델을 만듦)
    public static User fromEntity(UserEntity userEntity) {
        return new User(
                userEntity.getId(),         //서비스에 필요한 필드만 사용
                userEntity.getUserName(),
                userEntity.getPassword(),
                userEntity.getRole(),
                userEntity.getRegisteredAt(),
                userEntity.getUpdatedAt(),
                userEntity.getDeletedAt()
        );
    }


    /*
    getAuthorities 메서드를 오버라이드하여 UserRole에 따라 권한(GrantedAuthority)을 설정
    ㄴ>  권한 기반의 인증이 필요하다면 getAuthorities 메서드를 통해 권한을 설정하는 것이 유용함
    -
    참고 @Override 어노테이션 : User 클래스는 UserDetails 인터페이스를 구현하고 있으므로, UserDetails 인터페이스에 있는 메서드들을 사용하려면
    User 클래스에서 이 메서드들을 구현하기 위해 @Override를 사용해야함. (ex) 사용자의 권한을 반환하는 getAuthorities(), 사용자 이름을 반환하는 getUsername() 등의 메서드~인터페이스에 이미 정의되어 있음)
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {    //getAuthorities: 사용자의 역할을 권한으로 변환하여 Spring Security가 인식할 수 있게 함
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getUsername() {
        return userName;
    }



    /*
    < 계정 상태 메서드 >
    *User 클래스와 Spring Security와 통합이 필요한 경우* (인증, 권한 관리, 추가적인 계정 상태 관리 등의 기능 사용)
    -> UserDetails 인터페이스를 구현하고 상태 체크 메서드를 정의하는 것이 좋음
     */

    /*
    계정이 만료되지 않았는지를 확인
    ㄴ> deletedAt 필드를 기준으로 계정의 활성 상태를 판단하도록 설정
    : deletedAt == null  ->  계정이 만료되지 않음을 의미 ~ true 반환
      deletedAt이 null이 아닌 경우 -> 계정이 만료된 상태로 간주 ~ false 반환
     */
    @Override
    public boolean isAccountNonExpired() {
        return deletedAt == null;
    }

    /*
    계정이 잠겨 있지 않은지를 확인
     */
    @Override
    public boolean isAccountNonLocked() {
        return deletedAt == null;
    }

    /*
    자격 증명이 만료되지 않았는지를 확인
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return deletedAt == null;
    }

    /*
    계정이 활성 상태인지를 확인
     */
    @Override
    public boolean isEnabled() {
        return deletedAt == null;
    }
}

