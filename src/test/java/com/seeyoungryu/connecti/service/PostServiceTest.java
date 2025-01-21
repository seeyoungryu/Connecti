package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.fixture.PostEntityFixture;
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


    @Test
    @DisplayName("Post 생성 시 유저 미존재 에러 발생")
    void testCreatePostErrorUserNotFound() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty()); //유저 존재하지 않음
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        ConnectiApplicationException exception = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.createPost(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @DisplayName("Post 수정 성공")
    void testModifyPostSuccess() {

        String title = "title";
        String body = "body";
        String userName = "userName";
        Long postId = 1L;

// fixture 로 바꾸기 전 모킹으로 진행하려 했을 떄 코드
//        //mocking
//        PostEntity mockPostEntity = mock(PostEntity.class);
//        //when
//        when(userEntityRepository.findByUserName("userName")).thenReturn(Optional.of(mock(UserEntity.class)));
//        when(postEntityRepository.save(any())).thenReturn(Optional.of(mock(PostEntity.class)));


        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }


    @Test
    @DisplayName("Post 수정 시 Post 미존재 에러 발생")
    void testModifyPostErrorPostNotFound() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();
        UserEntity writer = UserEntityFixture.get(userName, "password");

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }


    @Test
    @DisplayName("Post 수정 시 유저 미존재 에러 발생")
    void testModifyPostErrorUserNotFound() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity writer = UserEntityFixture.get(userName, "password");

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));     //데이터베이스를 사용하는 로직을 모킹(mocking)하여 테스트 환경을 격리시킴.
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 성공")
    void testDeletePostSuccess() {

        String userName = "userName";
        Long postId = 1L;

        // Fixture 사용
        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        // Mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.deletePost(1L));
    }

    @Test
    @DisplayName("Post 삭제 시 Post 미존재 에러 발생")
    void testDeletePostErrorPostNotFound() {

        String userName = "userName";
        Long postId = 1L;

        // Fixture 사용
        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        // Mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.deletePost(1L));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 시 유저 미존재 에러 발생")
    void testDeletePostErrorUserNotFound() {

        String userName = "userName";
        Long postId = 1L;

        // Fixture 사용
        PostEntity postEntity = PostEntityFixture.get(userName, postId);

        // Mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.deletePost(1L));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 시 작성자 불일치 에러 발생")
    void testDeletePostErrorUserNotAuthor() {

        String userName = "userName";
        Long postId = 1L;

        // Fixture 사용
        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity otherUser = UserEntityFixture.get("otherUser", "password");

        // Mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(otherUser));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.deletePost(1L));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

}

