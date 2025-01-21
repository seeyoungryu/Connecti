package com.seeyoungryu.connecti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.request.PostModifyRequest;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @MockBean
    PostService postService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    /*
    포스트 작성
     */
    @Test
    @WithMockUser //인증된 유저 (없으면 인증 오류(401 Unauthorized)가 발생할 수 있음.)
    @DisplayName("포스트 작성 성공")
    void createPost_Success() throws Exception {

        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithAnonymousUser   // 익명의 유저로 요청 ~ 가정 (mocking 을 하지 않아도 됨)
    @DisplayName("로그인하지 않은 상태에서 포스트 작성 시 에러 발생")
    void createPost_UnauthorizedError() throws Exception {

        String title = "title";
        String body = "body";


        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));    // =? status().inUnauthorized()
    }


    /*
    포스트 수정
     */
    @Test
    @WithMockUser
    @DisplayName("포스트 작성 성공")
    void updatePost_Success() throws Exception {

        String title = "title";
        String body = "body";

        mockMvc.perform(put("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 상태에서 포스트 수정 시 에러 발생")
    void modifyPost_UnauthorizedError() throws Exception {

        String title = "title";
        String body = "body";


        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("본인이 작성한 글이 아닌 포스트 수정 시 에러 발생")
    void modifyPost_InvalidPermissionError() throws Exception {

        String title = "title";
        String body = "body";

        //이 부분에 본인이 작성한 글이 아니라는 모킹이 필요함
        doThrow(new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modifyPost(eq(title), eq(body), any(), eq(1L));  // title, body, uesrName, 1L => any() , eq(1L)

        mockMvc.perform(put("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser
    @DisplayName("수정하려는 포스트가 없을 경우 에러 발생")
    void modifyPost_PostNotFoundError() throws Exception {

        String title = "title";
        String body = "body";


        doThrow(new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modifyPost(eq(title), eq(body), any(), eq(1L));
        mockMvc.perform(put("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /*
    포스트 삭제
     */
    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("포스트 삭제 성공")
    void deletePost_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("본인이 작성한 글이 아닌 포스트 삭제 시 에러 발생")
    void deletePost_InvalidPermissionError() throws Exception {
        doThrow(new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION))
                .when(postService).deletePost(eq("testUser"), eq(1L));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("삭제하려는 포스트가 없을 경우 에러 발생")
    void deletePost_PostNotFoundError() throws Exception {
        doThrow(new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).deletePost(eq("testUser"), eq(1L));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}

