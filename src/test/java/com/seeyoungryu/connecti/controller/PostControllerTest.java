package com.seeyoungryu.connecti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.request.PostModifyRequest;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.Post;
import com.seeyoungryu.connecti.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @WithMockUser //인증된 유저 ~ 인증 오류(401 Unauthorized)방지
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
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(1L, "title", "body"))))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 상태에서 포스트 수정 시 에러 발생")
    void modifyPost_UnauthorizedError() throws Exception {

        String title = "title";
        String body = "body";


        mockMvc.perform(put("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(1L, "title", "body"))))
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
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(1L, "title", "body"))))
                .andDo(print())
                .andExpect(status().isForbidden());
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
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(1L, "title", "body"))))
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
        mockMvc.perform(delete("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("포스트 삭제시 로그인 하지 않은 경우 에러 발생")
    void deletePost_UnauthorizedError() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("본인이 작성한 글이 아닌 포스트 삭제 시 에러 발생")
    void deletePost_InvalidPermissionError() throws Exception {
        doThrow(new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION))
                .when(postService).deletePost(eq("testUser"), eq(1L));

        mockMvc.perform(delete("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("삭제하려는 포스트가 없을 경우 에러 발생")
    void deletePost_PostNotFoundError() throws Exception {
        doThrow(new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).deletePost(eq("testUser"), eq(1L));

        mockMvc.perform(delete("/api/v1/posts/1L")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    /*
     피드 목록(포스트들의 리스트) 가져오기
    */
    @Test
    @WithMockUser
    @DisplayName("피드 목록 요청 시 성공")
    void returnFeedList_Success() throws Exception {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10); // 페이지 번호 0, 크기 10

        // Mock 데이터 생성: Post 리스트
        List<Post> mockPostList = List.of(
                new Post(1L, "Title 1", "Body 1", null, null, null),
                new Post(2L, "Title 2", "Body 2", null, null, null)
        );
        Page<Post> mockPage = new PageImpl<>(mockPostList); // Page<Post> 생성

        // postService.list(pageable) 모킹
        when(postService.list(pageable)).thenReturn(mockPage);

        // MockMvc 테스트
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0") // 요청 파라미터로 페이지 번호 전달
                        .param("size", "10")) // 요청 파라미터로 페이지 크기 전달
                .andDo(print())
                .andExpect(status().isOk()) // HTTP 200 상태 코드 기대
                .andExpect(jsonPath("$.content.length()").value(mockPostList.size())) // JSON 배열 크기 확인
                .andExpect(jsonPath("$.content[0].title").value("Title 1")) // 첫 번째 아이템 확인
                .andExpect(jsonPath("$.content[1].body").value("Body 2")); // 두 번째 아이템 확인
    }

    /* <상태 코드만 테스트 하는 경우>
    @Test
    @WithMockUser
    @DisplayName("피드 목록 요청 시 성공")
    void 피드목록() throws Exception {
        // postService.list() 메서드 모킹 - 빈 페이지 반환
        when(postService.list(any())).thenReturn(Page.empty());

        // MockMvc를 사용한 API 호출 및 검증
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()); // HTTP 상태 코드 200 확인
    }
     */


    /*
     * 로그인되지 않은 사용자가 피드 목록 요청 시 에러 발생 테스트
     */
    @Test
    @WithAnonymousUser
    @DisplayName("로그인되지 않은 사용자가 피드 목록 요청 시 에러 발생")
    void returnFeedList_UnauthorizedError() throws Exception {
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    /*
     * 나의 피드 목록 가져오기 성공 테스트
     */
    @Test
    @WithMockUser
    @DisplayName("나의 피드 목록 요청 시 성공")
    void returnMyFeedList_Success() throws Exception {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);

        // Mock 데이터 생성: Post 리스트
        List<Post> mockPostList = List.of(
                new Post(1L, "My Title 1", "My Body 1", null, null, null),
                new Post(2L, "My Title 2", "My Body 2", null, null, null)
        );
        Page<Post> mockPage = new PageImpl<>(mockPostList);

        // postService.myList(userName, pageable) 모킹
        Mockito.when(postService.myList(any(), pageable)).thenReturn(mockPage);

        // MockMvc 테스트
        mockMvc.perform(get("/api/v1/posts/myFeed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(mockPostList.size()))
                .andExpect(jsonPath("$.content[0].title").value("My Title 1"))
                .andExpect(jsonPath("$.content[1].body").value("My Body 2"));
    }

    /*
     * 로그인되지 않은 사용자가 나의 피드 목록 요청 시 에러 발생 테스트
     */
    @Test
    @WithAnonymousUser
    @DisplayName("로그인되지 않은 사용자가 나의 피드 목록 요청 시 에러 발생")
    void returnMyFeedList_UnauthorizedError() throws Exception {
        mockMvc.perform(get("/api/v1/posts/myFeed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
