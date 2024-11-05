package com.seeyoungryu.connecti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;



    /*
        포스트 작성 테스트 (성공)
     */

    @Test
    @WithMockUser //어노테이션 ~ 로그인 한 유저
    @DisplayName("포스트 작성 성공")
    public void testCreatePostSuccess() throws Exception {
        String title = "Test Title";
        String body = "Test body";

        PostEntity mockPostEntity = mock(PostEntity.class); // PostEntity 객체를 모킹
        when(postService.createPost("Test Title", "Test body", "SampleUserName")).thenReturn(mockPostEntity); // PostEntity 리턴 설정

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print())
                .andExpect(status().isOk());
    }



    /*
        포스트 작성 테스트 (실패-로그인 되지 않은 사용자)
     */

    @Test
    @DisplayName("포스트 작성 실패: 권한 인증되지 않은(로그인 하지 않은) 사용자")
    @WithAnonymousUser //로그인 안한 유저 ~어노테이션 @Todo : 필터 ? 무슨내용이지
    public void testCreatePostUnauthorized() throws Exception {
        String title = "Test Title";
        String body = "Test body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    /*
       포스트 작성 테스트 (실패-데이터베이스 오류)
     */

    @Test
    @WithMockUser
    @DisplayName("포스트 작성 실패: 데이터베이스 오류")
    public void testCreatePostDatabaseError() throws Exception {
        String title = "Test Title";
        String body = "Test body";

        when(postService.createPost("Test Title", "Test body"))
                .thenThrow(new ConnectiApplicationException(ErrorCode.DATABASE_ERROR));

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }



    /*
    포스트 작성 테스트 (실패-내부 서버 오류)
  */

    @Test
    @WithMockUser
    @DisplayName("포스트 작성 실패: 내부 서버 오류")
    public void testCreatePostInternalError() throws Exception {
        String title = "Test Title";
        String body = "Test body";

        when(postService.createPost("Test Title", "Test body"))
                .thenThrow(new ConnectiApplicationException(ErrorCode.INTERNAL_SERVER_ERROR));

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body))))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}
