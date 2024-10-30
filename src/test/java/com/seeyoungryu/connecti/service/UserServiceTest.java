package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.fixture.UserEntityFixture;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {


    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    /*
    회원가입 테스트 (성공, 실패)
        */
    @Test
    @DisplayName("회원가입이 정상적으로 동작할 경우")
    void testUserRegistrationSuccess() {
        String username = "testuser";
        String password = "password";

        // Mocking (mock 객체 반환을 위함)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.empty()); //empty -> 아직 회원가입 된 적 없기 때문임
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        Assertions.assertDoesNotThrow(() -> userService.join(username, password));
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 사용자명으로 인한 에러")
    void testUserRegistrationFailsDueToDuplicateUsername() {
        String username = "testuser";
        String password = "password";

        // Mocking (mock 객체 반환을 위함)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.of(mock(UserEntity.class))); // 이미 존재하는 유저로 가정
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        Assertions.assertThrows(ConnectiApplicationException.class, () -> userService.join(username, password));
        //join 시, 이미 회원가입한 유저가 있으므로, 에러를 던져줘야(throw해야함) -> 던져준 에러(exeption)로 컨트롤러 단에서 에러를 처리 할 수 있도록!
    }


    // @Todo : 위의 코드는 현재 mock 객체로 사용중, fixture 로 바꾸는 코드 확인하기, 이유


    /*
    로그인 테스트 (성공, 에러(미가입, 잘못된 패스워드))
     */

    @Test
    @DisplayName("로그인 성공: 올바른 사용자명과 비밀번호")
    void testUserLoginSuccess() {
        String userName = "testuser";
        String password = "password";

        //mock 대신에 fixture 사용
        UserEntity fixture = UserEntityFixture.get(userName, password);// -> get -> 테스트용 가짜 userentity 가 리턴됨.
        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));


    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 사용자명")
    void testLoginWithUnregisteredUserReturnsError() {
        String userName = "testuser";
        String password = "password";

        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        Assertions.assertThrows(ConnectiApplicationException.class, () -> userService.login(userName, password));

    }


    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호")
    void testLoginWithIncorrectPasswordReturnsError() {
        String userName = "testuser";
        String password = "password";
        String wrongPassword = "wrongPassword"; //로그인 실패

        UserEntity fixture = UserEntityFixture.get(userName, password);

        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        Assertions.assertThrows(ConnectiApplicationException.class, () -> userService.login(userName, wrongPassword));  // 잘못된 패스워드


    }

}



/*
이 `UserServiceTest` 클래스는 **회원가입과 로그인 기능의 성공과 실패에 대한 테스트**를 수행합니다. 이 테스트 클래스의 각 부분과 동작을 설명해드릴게요.

### 클래스 설명 및 각 메서드 동작

1. **`@SpringBootTest`**
   Spring Boot의 모든 설정을 로드하여 **통합 테스트**를 수행합니다. 이 어노테이션 덕분에 실제 애플리케이션 환경과 유사하게 테스트할 수 있습니다.

2. **`@MockBean`과 `@Autowired`**
   - **`@MockBean`**: `UserEntityRepository`를 목(mock)으로 설정해 실제 DB 접근 없이 가짜 객체를 이용해 테스트합니다.
   - **`@Autowired`**: `UserService`는 실제 서비스 클래스가 주입되며, 테스트 대상 메서드의 호출이 이루어집니다.

### 메서드별 테스트 설명

#### 회원가입 테스트
1. **성공 테스트 - `testUserRegistrationSuccess`**
   - **Mocking**: `userEntityRepository.findByUserName`은 비어 있는 `Optional`을 반환하도록 설정하여, 해당 사용자명이 없는 상태로 가정합니다.
   - `userEntityRepository.save`는 임의의 `UserEntity`를 반환하게 해 회원 정보 저장이 완료된 것으로 가정합니다.
   - **Assertions**: `Assertions.assertDoesNotThrow`를 통해 `userService.join` 호출 시 예외가 발생하지 않으면 성공으로 간주합니다.

2. **실패 테스트 (중복 사용자명) - `testUserRegistrationFailsDueToDuplicateUsername`**
   - **Mocking**: `userEntityRepository.findByUserName`이 이미 존재하는 사용자로 가정하고, 이미 가입된 사용자 객체를 반환하도록 설정합니다.
   - **Assertions**: `Assertions.assertThrows`는 `userService.join`이 중복 사용자 예외(ConnectiApplicationException)를 던져야만 성공입니다.

#### 로그인 테스트
1. **성공 테스트 - `testUserLoginSuccess`**
   - **Fixture 사용**: `UserEntityFixture.get`을 이용해 테스트용 사용자 객체를 생성합니다.
   - **Mocking**: `userEntityRepository.findByUserName`이 해당 사용자명을 반환하도록 설정해 사용자 인증이 통과되게 합니다.
   - **Assertions**: `Assertions.assertDoesNotThrow`로 예외 발생이 없을 때 로그인 성공으로 판단합니다.

2. **실패 테스트 (미등록 사용자) - `testLoginWithUnregisteredUserReturnsError`**
   - **Mocking**: `userEntityRepository.findByUserName`이 비어 있는 `Optional`을 반환해 미가입 상태를 가정합니다.
   - **Assertions**: `Assertions.assertThrows`가 `ConnectiApplicationException` 예외를 반환해야 성공입니다.

3. **실패 테스트 (잘못된 비밀번호) - `testLoginWithIncorrectPasswordReturnsError`**
   - **Fixture 사용**: 테스트용 사용자 객체를 Fixture에서 가져옵니다.
   - **Mocking**: `userEntityRepository.findByUserName`이 사용자 객체를 반환하도록 설정합니다.
   - **Assertions**: 잘못된 비밀번호로 로그인할 때 `ConnectiApplicationException` 예외가 발생해야 성공입니다.

### Mock 객체와 Fixture의 차이점

- **Mock**은 메서드가 호출될 때 **특정 동작을 흉내 내도록 설정**하는 데 유리합니다.
- **Fixture**는 실제 사용될 가짜 객체를 만들어 필요한 데이터를 그대로 제공하기 때문에 **실제 객체처럼 동작**합니다.

이를 통해 이 테스트는 가짜 환경에서의 회원가입과 로그인 시도 전 과정을 재현하고, 기대된 동작을 검증하는 구조로 설계되었습니다.
 */