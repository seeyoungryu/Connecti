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
    @WithMockUser
    @DisplayName("포스트 작성 성공")
    public void testCreatePostSuccess() throws Exception {
        String title = "Test Title";
        String content = "Test Content";

        PostEntity mockPostEntity = mock(PostEntity.class); // PostEntity 객체를 모킹
        when(postService.createPost("Test Title", "Test Content")).thenReturn(mockPostEntity); // PostEntity 리턴 설정

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, content))))
                .andDo(print())
                .andExpect(status().isOk());
    }


//    @Test
//    @WithMockUser
//    @DisplayName("포스트 작성 성공")
//    public void testCreatePostSuccess() throws Exception {
//        String title = "Test Title";
//        String content = "Test Content";
//
//        Post mockPost = mock(Post.class);
//        when(postService.createPost("Test Title", "Test Content")).thenReturn(mockPost);
//
//        mockMvc.perform(post("/api/v1/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, content))))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }




    /*
        포스트 작성 테스트 (실패-로그인 되지 않은 사용자)
     */

    @Test
    @DisplayName("포스트 작성 실패: 인증되지 않은 사용자")
    public void testCreatePostUnauthorized() throws Exception {
        String title = "Test Title";
        String content = "Test Content";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, content))))
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
        String content = "Test Content";

        when(postService.createPost("Test Title", "Test Content"))
                .thenThrow(new ConnectiApplicationException(ErrorCode.DATABASE_ERROR));

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, content))))
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
        String content = "Test Content";

        when(postService.createPost("Test Title", "Test Content"))
                .thenThrow(new ConnectiApplicationException(ErrorCode.INTERNAL_SERVER_ERROR));

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, content))))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}
