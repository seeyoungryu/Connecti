package com.seeyoungryu.connecti.service;

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


    @Test
    @DisplayName("회원가입이 정상적으로 동작할 경우")
    void testUserRegistrationSuccess() {
        String username = "testuser";
        String password = "password";

        // Todo: add Mocking (mock 객체 반환을 위함)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.empty()); //empty -> 아직 회원가입 된 적 없기 때문임
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        Assertions.assertDoesNotThrow(() -> userService.join(username, password));
    }


}
