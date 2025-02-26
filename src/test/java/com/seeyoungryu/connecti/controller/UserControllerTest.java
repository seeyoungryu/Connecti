package com.seeyoungryu.connecti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeyoungryu.connecti.controller.request.UserJoinRequest;
import com.seeyoungryu.connecti.controller.request.UserLoginRequest;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.service.AlarmService;
import com.seeyoungryu.connecti.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AlarmService alarmService;

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
    로그인 테스트 (성공, 실패)
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
    @DisplayName("로그인 실패 - 미가입 유저")
    public void testLoginWithUnregisteredUserReturnsError() throws Exception {
        String userName = "testuser";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
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
    알람 기능 테스트
    */
    @Test
    @WithMockUser
    @DisplayName("알람 조회 성공")
    void getAlarms_Success() throws Exception {
        // Given
        Page<AlarmEntity> alarmPage = new PageImpl<>(List.of(new AlarmEntity()));

        when(alarmService.getAlarms(any(), any())).thenReturn(alarmPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/alarms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("알람 조회 - 알람이 없는 경우")
    void getAlarms_Empty() throws Exception {
        // Given
        Page<AlarmEntity> emptyPage = Page.empty();

        when(alarmService.getAlarms(any(), any())).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users/alarms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()); // 응답은 200 OK여야 하지만 데이터는 비어 있음
    }

    @Test
    @WithAnonymousUser
    @DisplayName("알람 조회 실패 - 로그인하지 않은 유저")
    void getAlarms_ErrorUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/alarms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized()); // 401 Unauthorized 반환
    }
}
