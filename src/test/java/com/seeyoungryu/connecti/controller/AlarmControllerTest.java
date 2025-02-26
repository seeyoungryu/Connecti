package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlarmService alarmService;

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("알람 조회 성공")
    void getAlarms_Success() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<AlarmEntity> alarmList = List.of(new AlarmEntity());
        Page<AlarmEntity> alarmPage = new PageImpl<>(alarmList);

        when(alarmService.getAlarms(any(), any(Pageable.class))).thenReturn(alarmPage);

        // When & Then
        mockMvc.perform(get("/api/v1/alarms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알람 조회 실패 - 로그인하지 않은 유저")
    void getAlarms_ErrorUnauthenticatedUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/alarms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized()); // 401 Unauthorized 반환
    }
}


/*
@WebMvcTest(AlarmController.class)

AlarmController만을 대상으로 하는 테스트임.
MockMvc를 사용하여 HTTP 요청을 시뮬레이션함.
@WithMockUser(username = "testUser")

getAlarms_Success()에서 인증된 사용자(testUser)로 요청을 수행하는 역할.
Spring Security가 활성화된 상태에서도 인증된 사용자처럼 동작하도록 설정.
mockMvc.perform(get("/api/v1/alarms")...)

실제 컨트롤러 엔드포인트(/api/v1/alarms)를 호출하여 응답을 검증.
성공 테스트 (getAlarms_Success())

alarmService.getAlarms()가 정상적으로 호출될 때, 200 OK 응답을 반환하는지 확인.
실패 테스트 (getAlarms_ErrorUnauthenticatedUser())

인증되지 않은 사용자가 요청했을 때 401 Unauthorized 응답을 반환하는지 확인.

 */
