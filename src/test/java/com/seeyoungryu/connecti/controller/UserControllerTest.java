package com.seeyoungryu.connecti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeyoungryu.connecti.controller.request.UserJoinRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@AutoConfigureMockMvc 어노테이션 (현재 API 형태의 컨트롤러를 테스트하는 중임)
//ㄴ> 웹 서버를 띄우지 않고도 컨트롤러의 API를 테스트할 수 있음->API 요청을 "모의"로 보내고 그 응답을 검증하는 테스트 가능

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    //실제 메서드 사용하려면 주입 필요함

    @Autowired
    private ObjectMapper objectMapper;
    //JSON 데이터를 Java 객체로 변환하거나, Java 객체를 JSON으로 변환하는 데 사용되는 [Jackson 라이브러리]의 클래스

    @Test
    @DisplayName("회원가입 테스트")
    public void testUserRegistration() throws Exception {
        String username = "testuser1";
        String password = "password1";

        //@Todo mocking 필요

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(username, password))) // : Request body -> .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("중복된 사용자명으로 회원가입 시 에러 반환")
    public void testRegistrationWithDuplicateUsernameReturnsError() throws Exception {
        String username = "testuser1";
        String password = "password1";

        //@Todo mocking 필요

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(username, password))) // : Request body -> .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
                ).andDo(print())
                .andExpect(status().isConflict());
    }
}
