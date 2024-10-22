package com.seeyoungryu.connecti.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원가입이 정상적으로 동작할 경우")
    void testUserRegistrationSuccess() {
        String username = "testuser";
        String password = "password";

        Assertions.assertDoesNotThrow(() -> userService.join(username, password));
    }


}
