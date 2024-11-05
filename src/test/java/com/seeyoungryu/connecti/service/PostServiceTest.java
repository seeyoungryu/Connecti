package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository PostEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;


    /*
    Post 작성 성공
     */
    @Test
    @DisplayName("Post 작성 성공")
    void testCreatePostSuccess() {
        String title = "Sample Title";
        String body = "Sample body";
        String userName = "sampleUserName";

        //moking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(PostEntityRepository.save(any(PostEntity.class))).thenReturn(mock(PostEntity.class));


        PostEntity mockPost = new PostEntity(title, body);
        when(PostEntityRepository.save(any())).thenReturn(mockPost);

        Assertions.assertDoesNotThrow(() -> postService.createPost(title, body, userName));   // @Todo : 왜 여기서는 에러를 스로우 하면 안됨?

    }


    /*
    Post 작성 실패 : 작성 요청 시 존재하지 않는 유저인 경우
     */
    @Test
    @DisplayName("Post 조회 실패: 존재하지 않는 ID")
    void testGetPostFailureDueToNonexistentId() {

        String title = "Sample Title";
        String body = "Sample body";
        String userName = "SampleUserName";

        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());   //유저 데이터가 없음 (비어있음)
        when(PostEntityRepository.save(any(PostEntity.class))).thenReturn(mock(PostEntity.class));


        ConnectiApplicationException e = assertThrows(ConnectiApplicationException.class, () -> postService.createPost(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }


    // Todo  Assertions.assertEquals 를 그냥  assertEquals 로만 써도 되는지?


     /*
    Post 작성 실패 : 작성 요청 시 존재하지 않는 유저인 경우
     */

    @Test
    @DisplayName("Post 작성 실패: 제목 또는 본문 누락")
    void testCreatePostFailureDueToInvalidInput() {
        assertThrows(ConnectiApplicationException.class, () -> postService.createPost(null, "body"));
        assertThrows(ConnectiApplicationException.class, () -> postService.createPost("Title", null));
    }


     /*
    Post 작성 실패 : 작성 요청 시 존재하지 않는 유저인 경우
     */

    @Test
    @DisplayName("Post 조회 성공")
    void testGetPostSuccess() {
        Long postId = 1L;
        PostEntity mockPost = new PostEntity("Sample Title", "Sample body");
        when(PostEntityRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        PostEntity post = postService.getPost(postId);
        assertNotNull(post);
    }


     /*
    Post 작성 실패 : 작성 요청 시 존재하지 않는 유저인 경우
     */

    @Test
    @DisplayName("Post 삭제 성공")
    void testDeletePostSuccess() {
        Long postId = 1L;
        PostEntity mockPost = new PostEntity("Sample Title", "Sample body");
        when(PostEntityRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        assertDoesNotThrow(() -> postService.deletePost(postId));
    }


    /*
   Post 작성 실패 : 작성 요청 시 존재하지 않는 유저인 경우
    */
    @Test
    @DisplayName("Post 삭제 실패: 존재하지 않는 ID")
    void testDeletePostFailureDueToNonexistentId() {
        Long postId = 999L;
        when(PostEntityRepository.findById(postId)).thenReturn(Optional.empty());

        ConnectiApplicationException e = assertThrows(ConnectiApplicationException.class, () -> postService.deletePost(postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }
}
