package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.fixture.TestInfoFixture;
import com.seeyoungryu.connecti.fixture.UserEntityFixture;
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
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    PostService postService;

    @MockBean
    PostEntityRepository postEntityRepository;

    @MockBean
    UserEntityRepository userEntityRepository;



    /*
   포스트 작성 성공 테스트
    */

    @Test
    @DisplayName("Post 생성 성공")
    void testCreatePostSuccess() {

        String title = "title";
        String body = "body";
        String userName = "userName";


        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        //* mocking *
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));
        //fixture 미사용시 코드 = when(userEntityRepository.findByUserName("userName")).thenReturn(Optional.of(new UserEntity("userName", "encodedPassword")));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.createPost(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));
        // assertDoesNotThrow - 성공시 에러 스로우 하면 안되므로
    }


    /*
    포스트 실패 - 유저가 존재하지 않는 경우
     */
    @Test
    @DisplayName("Post 생성 시 유저 미존재 에러 발생")
    void testCreatePostErrorUserNotFound() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();   // Todo 이 코드줄의 존재 의미가 뭐임 ..?
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty()); //유저 존재하지 않음
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));
        
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.createPost(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    /*
    조회
     */
    @Test
    @DisplayName("내 Post 리스트 조회 시 유저 미존재 에러 발생")
    void testFetchMyPostsErrorUserNotFound() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.my(fixture.getUserName(), mock(Pageable.class)));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Post 수정 시 Post 미존재 에러 발생")
    void testModifyPostErrorPostNotFound() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () ->
                postService.modify(fixture.getUserName(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Post 수정 시 유저 미존재 에러 발생")
    void testModifyPostErrorUserNotFound() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.modify(fixture.getUserName(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Post 수정 시 작성자 불일치 에러 발생")
    void testModifyPostErrorUserNotAuthor() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.modify(fixture.getUserName(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 시 Post 미존재 에러 발생")
    void testDeletePostErrorPostNotFound() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.deletePost(fixture.getUserName(), fixture.getPostId()));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 시 유저 미존재 에러 발생")
    void testDeletePostErrorUserNotFound() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.deletePost(fixture.getUserName(), fixture.getPostId()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 시 작성자 불일치 에러 발생")
    void testDeletePostErrorUserNotAuthor() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.deletePost(fixture.getUserName(), fixture.getPostId()));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }
}
