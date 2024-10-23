package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
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


    /*
    로그인 테스트 (성공, 에러(미가입, 잘못된 패스워드))
     */
    @Test
    @DisplayName("로그인 성공: 올바른 사용자명과 비밀번호")
    void testUserLoginSuccess() {
        String username = "testuser";
        String password = "password";

        // Mocking (mock 객체 반환을 위함)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.empty());
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        Assertions.assertDoesNotThrow(() -> userService.login(username, password));
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 사용자명")
    void testLoginWithUnregisteredUserReturnsError() {
        String username = "testuser";
        String password = "password";

        // 내부 코드
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호")
    void testLoginWithIncorrectPasswordReturnsError() {
        String username = "testuser";
        String password = "password";

        // 내부 코드
    }

}
