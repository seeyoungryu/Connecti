package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository PostEntityRepository;

    @Test
    @DisplayName("Post 작성 성공")
    void testCreatePostSuccess() {
        String title = "Sample Title";
        String body = "Sample body";

        PostEntity mockPost = new PostEntity(title, body);
        when(PostEntityRepository.save(any())).thenReturn(mockPost);

        assertDoesNotThrow(() -> postService.createPost(title, body));
    }

    @Test
    @DisplayName("Post 작성 실패: 제목 또는 본문 누락")
    void testCreatePostFailureDueToInvalidInput() {
        assertThrows(ConnectiApplicationException.class, () -> postService.createPost(null, "body"));
        assertThrows(ConnectiApplicationException.class, () -> postService.createPost("Title", null));
    }

    @Test
    @DisplayName("Post 조회 성공")
    void testGetPostSuccess() {
        Long postId = 1L;
        PostEntity mockPost = new PostEntity("Sample Title", "Sample body");
        when(PostEntityRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        PostEntity post = postService.getPost(postId);
        assertNotNull(post);
    }

    @Test
    @DisplayName("Post 조회 실패: 존재하지 않는 ID")
    void testGetPostFailureDueToNonexistentId() {
        Long postId = 999L;
        when(PostEntityRepository.findById(postId)).thenReturn(Optional.empty());

        ConnectiApplicationException e = assertThrows(ConnectiApplicationException.class, () -> postService.getPost(postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 성공")
    void testDeletePostSuccess() {
        Long postId = 1L;
        PostEntity mockPost = new PostEntity("Sample Title", "Sample body");
        when(PostEntityRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        assertDoesNotThrow(() -> postService.deletePost(postId));
    }

    @Test
    @DisplayName("Post 삭제 실패: 존재하지 않는 ID")
    void testDeletePostFailureDueToNonexistentId() {
        Long postId = 999L;
        when(PostEntityRepository.findById(postId)).thenReturn(Optional.empty());

        ConnectiApplicationException e = assertThrows(ConnectiApplicationException.class, () -> postService.deletePost(postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }
}
