package com.seeyoungryu.connecti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeyoungryu.connecti.controller.request.UserJoinRequest;
import com.seeyoungryu.connecti.controller.request.UserLoginRequest;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    //실제 메서드 사용하려면 주입 필요함

    @Autowired
    private ObjectMapper objectMapper;
    //JSON 데이터를 Java 객체로 변환하거나, Java 객체를 JSON으로 변환하는 데 사용되는 [Jackson 라이브러리]의 클래스

    @MockBean // 테스트에 필요한 userService를 모킹하여 실제 인스턴스가 아닌 테스트용 객체를 주입
    private UserService userService;


    /*
    회원가입 테스트 (성공)
    */
    @Test
    @WithAnonymousUser
    @DisplayName("회원가입 테스트")
    public void testUserRegistration() throws Exception {
        String userName = "name";
        String password = "password";

        when(userService.join(userName, password)).thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest("name", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
    }


    /*
    회원가입 테스트 (실패)
    */
    @Test
    @WithMockUser
    @DisplayName("중복된 사용자명으로 회원가입 시 에러 반환")
    public void testRegistrationWithDuplicateUsernameReturnsError() throws Exception {
        String userName = "name";
        String password = "password";
        when(userService.join(userName, password)).thenThrow(new ConnectiApplicationException(ErrorCode.DUPLICATED_USER_NAME));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest("name", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_USER_NAME.getStatus().value()));
    }

    /*
    로그인 테스트 (성공, 에러(미가입, 잘못된 패스워드))
     */
    @Test
    @WithMockUser
    @DisplayName("로그인 테스트")
    public void testUserLoginSuccess() throws Exception {
        String userName = "name";
        String password = "password";

        when(userService.login(userName, password)).thenReturn("testToken");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("name", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithAnonymousUser
    @DisplayName("로그인 테스트(로그인시 회원가입이 되지 않은 userName 입력시 에러 반환)")
    public void testLoginWithUnregisteredUserReturnsError() throws Exception {
        String userName = "testuser";
        String password = "password";

        // * mocking *
        User mockUser = mock(User.class);
        when(userService.login(userName, password)).thenThrow(new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인 테스트(로그인시 잘못된 password 입력시 에러 반환)")
    public void testLoginWithIncorrectPasswordReturnsError() throws Exception {
        String userName = "name";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new ConnectiApplicationException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("name", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }


    /*
    알람 기능
     */
    @Test
    @WithMockUser //Given (준비)
    @DisplayName("알람 기능 정상 동작")
    void getAlarms_Success() throws Exception {
        //mocking
        when(userService.alarmsList(any(), any())).thenReturn(Page.empty());


        mockMvc.perform(post("/api/v1/users/alrams") //given(준비) : mockMvc.perform(post(...))로 HTTP 요청을 생성 , when(실행) : 실제 /api/v1/users/alrams API를 호출.
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()); //Then(검증) : API 응답 확인
    }

    @Test
    @WithAnonymousUser
    @DisplayName("알람 조회 실패 - 로그인하지 않은 유저")
    void getAlarms_ErrorUnauthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/v1/users/alarms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized()); // 401 Unauthorized 반환
    }

}


