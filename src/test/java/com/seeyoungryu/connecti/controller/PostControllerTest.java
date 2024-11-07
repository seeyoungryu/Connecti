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
import org.springframework.http.HttpHeaders;
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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    private ObjectMapper objectMapper;


    /*
    포스트 작성 성공 테스트
     */
    @Test
    @WithMockUser //인증된 유저
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


    /*
    포스트 작성 실패 테스트 (로그인 하지 않은 사용자)
     */

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

     */
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
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser
    @DisplayName("본인이 작성한 글이 아닌 포스트 수정 시 에러 발생")
    void modifyPost_InvalidPermissionError() throws Exception {

        String title = "title";
        String body = "body";


        doThrow(new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(any(), eq(1L), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser
    @DisplayName("수정하려는 포스트가 없을 경우 에러 발생")
    void modifyPost_PostNotFoundError() throws Exception {

        String title = "title";
        String body = "body";


        doThrow(new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(any(), eq(1L), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithMockUser
    @DisplayName("데이터베이스 에러 발생 시 포스트 수정 에러 발생")
    void modifyPost_DatabaseError() throws Exception {

        String title = "title";
        String body = "body";


        doThrow(new ConnectiApplicationException(ErrorCode.DATABASE_ERROR)).when(postService).modify(any(), eq(1L), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest("title", "body"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 상태에서 포스트 삭제 시 에러 발생")
    void deletePost_UnauthorizedError() throws Exception {

        String title = "title";
        String body = "body";


        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser
    @DisplayName("본인이 작성한 글이 아닌 포스트 삭제 시 에러 발생")
    void deletePost_InvalidPermissionError() throws Exception {

        String title = "title";
        String body = "body";


        doThrow(new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).deletePost(any(), eq(1L));
        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser
    @DisplayName("삭제하려는 포스트가 없을 경우 에러 발생")
    void deletePost_PostNotFoundError() throws Exception {
        doThrow(new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).deletePost(any(), eq(1L));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithMockUser
    @DisplayName("데이터베이스 에러 발생 시 포스트 삭제 에러 발생")
    void deletePost_DatabaseError() throws Exception {
        doThrow(new ConnectiApplicationException(ErrorCode.DATABASE_ERROR)).when(postService).deletePost(any(), eq(1L));
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }
}
