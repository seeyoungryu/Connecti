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
