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


//@Todo : 전체적인 주석 정리 (흐름 파앍 후) 필요함


@SpringBootTest
public class UserServiceTest {


    @Autowired
    private UserService userService; //실제 서비스 클래스 주입,테스트 대상 메서드 호출
    @MockBean
    private UserEntityRepository userEntityRepository; //UserEntityRepository를 mock 으로 설정, 실제 디비 이용x, 가짜 객체를 이용해 테스트함


    /*
    회원가입 테스트 (성공)
        */
    @Test
    @DisplayName("회원가입이 정상적으로 동작할 경우")
    void testUserRegistrationSuccess() {
        String username = "testuser";
        String password = "password";

        //기존 mock 대신 Fixture 사용
        UserEntity fixture = UserEntityFixture.get(username, password);


        //Mocking (mock 객체 반환을 위함)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.empty()); //비어있는 Optional 을 반환하도록 하고 있음 (empty -> 사용자명이 없는 상태로 가정)
        when(userEntityRepository.save(any())).thenReturn(fixture); // Fixture 사용하여 UserEntity 반환 ( mock 사용 코드 : thenReturn(Optional.of(mock(UserEntity.class))); )

        Assertions.assertDoesNotThrow(() -> userService.join(username, password));
    }

    // @Todo : fixture -> mock 사용으로 변경 /완료
    // ㄴ mock(UserEntity.class) 대신에 -> UserEntityFixture 를 사용하는 방식으로 수정


    /*
    회원가입 테스트 ( 실패 - 중복된 사용자명 )
      */
    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 사용자명으로 인한 에러")
    void testUserRegistrationFailsDueToDuplicateUsername() {
        String username = "testuser";
        String password = "password";

        // Mocking (mock 객체 반환을 위함)
        // : 회원가입 테스트를 할 때 userEntityRepository.findByUserName가 데이터베이스에서 데이터를 찾는 대신 미리 정의된 <<가짜 데이터를 반환>>하도록 하는 것임 (가짜 UserEntity 객체가 반환됨)

        // * when 메서드 * : 조건과 상황을 가정하는 메서드
        // ㄴ> Mock 객체가 특정 입력을 받았을 때 어떤 동작을 수항할지 미리 설정함
        //  ㄴ> 메서드를 호출하면 항상 빈 Optional 을 반환하도록 상황을 가정
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.of(mock(UserEntity.class))); // 해당 username으로 가입된 유저가 없다는 조건을 테스트 환경에서 미리 가정하는 코드임
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        //Mocking 을 통해 -> mock(UserEntity.class) ~ 가짜 UserEntity 객체가 반환됨 ~ 실제 db와는 상관없이 UserEntity의 기능을 시뮬레이션 하는 객체 (실제 객체와 동일한 구조와 타입을 가지고 있으므로, 테스트 코드가 실제 상황과 비슷하게 작동하는지 확인할 수 있음)


        Assertions.assertThrows(ConnectiApplicationException.class, () -> userService.join(username, password));
        //join 시, 이미 회원가입한 유저가 있으므로, 에러를 던져줘야(throw해야함) -> 던져준 에러(exeption)로 컨트롤러 단에서 에러를 처리 할 수 있도록!
        //userService.join이 중복 사용자 예외(ConnectiApplicationException)를 던져야만 성공 (Assertions.assertThrows를 통해 userService.join 메서드를 호출할 때 ConnectiApplicationException 예외가 발생하는지 확인)

        /*
        Assertions.assertThrows는 특정 예외가 발생하는지 검증함. 여기서는 ConnectiApplicationException 예외가 userService.join(username, password) 호출 시 발생해야 테스트가 통과함.
        userService.join 메서드를 호출했을 때 중복 사용자 예외가 발생하는지를 검증하는 구조
        따라서 join 메서드 실행 중 중복된 사용자명이 확인되면, ConnectiApplicationException 예외를 던지고 테스트가 성공함.
         */


    }





    /*
    로그인 테스트 (성공)
    (변수 선언 → Mocking 설정 → 예외 발생 순서가 맞는지 여부)
     */

    @Test
    @DisplayName("로그인 성공: 올바른 사용자명과 비밀번호")
    void testUserLoginSuccess() {

        //변수 선언 -> 테스트에 필요한 사용자 정보를 설정
        String userName = "testuser";
        String password = "password";

        //mock 대신에 fixture 사용
        UserEntity fixture = UserEntityFixture.get(userName, password);// -> get -> 테스트용 가짜 userentity 가 리턴됨.

        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));


    }

    /*
    로그인 테스트 (실패 - 미가입)
     */
    @Test
    @DisplayName("로그인 실패: 존재하지 않는 사용자명")
    void testLoginWithUnregisteredUserReturnsError() {
        String userName = "testuser";
        String password = "password";

        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        Assertions.assertThrows(ConnectiApplicationException.class, () -> userService.login(userName, password));

    }

    /*
    로그인 테스트 (실패 - 잘못된 패스워드)
     */
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


