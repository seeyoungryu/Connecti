package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.fixture.PostEntityFixture;
import com.seeyoungryu.connecti.fixture.TestInfoFixture;
import com.seeyoungryu.connecti.fixture.UserEntityFixture;
import com.seeyoungryu.connecti.model.Post;
import com.seeyoungryu.connecti.model.entity.CommentEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.CommentEntityRepository;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
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

    @MockBean
    PostLikeService postLikeService; // add : 좋아요 서비스 추가

    @MockBean
    CommentEntityRepository commentEntityRepository; // add : 댓글 서비스 추가

    @MockBean
    CommentEntity commentEntity;


    @Test
    @DisplayName("Post 생성 성공")
    void testCreatePostSuccess() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        System.out.println("Fixture username: " + fixture.getUserName()); // 값 확인

        // Mocking
        UserEntity mockUser = UserEntityFixture.get("userName", "password"); // 값을 명확하게 지정
        when(userEntityRepository.findByUserName(anyString())).thenReturn(Optional.of(mockUser)); // eq() 대신 anyString() 사용

        // postEntityRepository.save() Mocking
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // 실행 및 검증
        Assertions.assertDoesNotThrow(() -> postService.createPost("userName", fixture.getTitle(), fixture.getBody()));
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


        // 디버깅 로그
        System.out.println("PostEntity: " + postEntity);
        System.out.println("PostEntity ID: " + postEntity.getId());
        System.out.println("PostEntity Title: " + postEntity.getTitle());
        System.out.println("PostEntity Body: " + postEntity.getBody());
        System.out.println("PostEntity User: " + postEntity.getUser());
        //PostEntity: com.seeyoungryu.connecti.model.entity.PostEntity@510d4d4b
        //PostEntity ID: 1
        //PostEntity Title: Test Title
        //PostEntity Body: Test Body
        //PostEntity User: com.seeyoungryu.connecti.model.entity.UserEntity@12f32f99 -> 디버깅 로그 정상 출력 (postEntityFixture.get 메서드 문제는 아님)

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        //Optional.of(postEntity)에서 postEntity가 null이라면, Optional.of(null)과 같은 상황이 되어 NullPointerException이 발생하거나 이후에 postEntity.getId()를 호출했을 때 문제가 발생합니다.

        Assertions.assertDoesNotThrow(() -> postService.modifyPost(title, body, userName, postId));
    }

    @Test
    @DisplayName("Post 수정 시 Post 미존재 에러 발생")
    void testModifyPostErrorPostNotFound() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Long postId = 1L;

        UserEntity userEntity = UserEntityFixture.get(userName, "password");

        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.of(userEntity));

        //Post가 존재하지 않도록 설정
        when(postEntityRepository.findById(postId))
                .thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.modifyPost(title, body, userName, postId));

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("Post 수정 시 작성자 불일치 에러 발생")
    void testModifyPostErrorUserNotOwner() {  // "유저 미존재" -> "작성자 불일치"
        String title = "title";
        String body = "body";
        String userName = "requestingUser";  // 요청한 유저
        String ownerName = "postOwner"; // 실제 작성자
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(ownerName, postId); // 작성자가 "postOwner"
        UserEntity requester = UserEntityFixture.get(userName, "password"); // 요청자는 "requestingUser"

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(requester));  // 요청자는 찾을 수 있음
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.modifyPost(title, body, userName, postId));
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

        Assertions.assertDoesNotThrow(() -> postService.deletePost("userName", 1L));
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
                () -> postService.deletePost("userName", 1L));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }


    // Todo 유저 미존재 or 작성자 불일치 .. 코드정리

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
                () -> postService.deletePost("userName", 1L));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("Post 삭제 시 작성자 불일치 에러 발생")
    void testDeletePostErrorUserNotOwner() {  // "작성자 불일치" 명확화
        String requesterName = "requestingUser";
        String ownerName = "postOwner";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(ownerName, postId);
        UserEntity requester = UserEntityFixture.get(requesterName, "password"); // 요청자는 작성자가 아님

        when(userEntityRepository.findByUserName(requesterName)).thenReturn(Optional.of(requester));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.deletePost(requesterName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }


    /*
     * 전체 피드 조회 성공 테스트
     */
    @Test
    @DisplayName("전체 피드 조회 성공")
    void testListFeedSuccess() {
        // Mock 데이터 생성
        Pageable pageable = PageRequest.of(0, 10);
        List<PostEntity> postEntities = List.of(
                PostEntityFixture.get("user1", 1L),
                PostEntityFixture.get("user2", 2L)
        );
        Page<PostEntity> postEntityPage = new PageImpl<>(postEntities);

        // Mocking
        when(postEntityRepository.findAll(pageable)).thenReturn(postEntityPage);

        // 서비스 호출
        Page<Post> result = postService.list(pageable);

        // 검증
        Assertions.assertEquals(2, result.getContent().size());

        // 기대값을 postId로 검증
        Assertions.assertEquals(1L, result.getContent().get(0).getId());
        Assertions.assertEquals(2L, result.getContent().get(1).getId());

        // 또는 실제 title 값을 기대값으로 설정
        Assertions.assertEquals("Test Title", result.getContent().get(0).getTitle());
        Assertions.assertEquals("Test Title", result.getContent().get(1).getTitle());
    }


    /*
     * 전체 피드 조회 시 비어 있는 결과 테스트
     */
    @Test
    @DisplayName("전체 피드 조회 시 비어 있는 결과 반환")
    void testListFeedEmptyResult() {
        // Mock 데이터 생성
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostEntity> emptyPage = Page.empty(pageable);

        // Mocking
        when(postEntityRepository.findAll(pageable)).thenReturn(emptyPage);

        // 서비스 호출
        Page<Post> result = postService.list(pageable);

        // 검증
        Assertions.assertTrue(result.isEmpty());
    }

    /*
     * 내 피드 조회 성공 테스트
     */


    /*
    @Test
    void 피드목록_조회_성공
    Pagebla pageble = mock(Pagebla.class);
    when(postEntityRepository.findAll(pageble)).thenReturn(Page.empty());
    Assertions.assertDoesNotThrow(()-> postService.list(pageble);
     */


    @Test
    @DisplayName("내 피드 조회 성공")
    void testMyFeedListSuccess() {
        // Mock 데이터 생성
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity userEntity = UserEntityFixture.get("user1", "password");

        List<PostEntity> postEntities = List.of(
                PostEntityFixture.get("user1", 1L),
                PostEntityFixture.get("user1", 2L)
        );
        Page<PostEntity> postEntityPage = new PageImpl<>(postEntities);

        // Mocking
        when(userEntityRepository.findByUserName("user1")).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findAllByUserId(1L, pageable)).thenReturn(postEntityPage);

        // 서비스 호출
        Page<Post> result = postService.myList("user1", pageable);

        // 검증 (postId를 검증)
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(1L, result.getContent().get(0).getId()); // 첫 번째 포스트 ID 검증
        Assertions.assertEquals(2L, result.getContent().get(1).getId()); // 두 번째 포스트 ID 검증
    }


    /*
     * 내 피드 조회 시 유저 미존재 에러 테스트
     */
    @Test
    @DisplayName("내 피드 조회 시 유저 미존재 에러 발생")
    void testMyFeedListErrorUserNotFound() {
        // Mock 데이터 생성
        Pageable pageable = PageRequest.of(0, 10);

        // Mocking
        when(userEntityRepository.findByUserName("nonexistentUser")).thenReturn(Optional.empty());

        // 검증
        ConnectiApplicationException e = Assertions.assertThrows(
                ConnectiApplicationException.class,
                () -> postService.myList("nonexistentUser", pageable)
        );
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }


    /*
     * 게시물 좋아요 테스트
     */
    @Test
    @DisplayName("게시물 좋아요 성공")
    void testLikePostSuccess() {
        String userName = "testUser";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.likePost(userName, postId));
    }

    @Test
    @DisplayName("게시물 좋아요 취소 성공")
    void testUnlikePostSuccess() {
        String userName = "testUser";
        Long postId = 1L;

        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.unlikePost(userName, postId));
    }

    /*
     * 댓글 작성 테스트
     */
    @Test
    @DisplayName("댓글 작성 성공")
    void testCreateCommentSuccess() {
        String userName = "testUser";
        Long postId = 1L;
        String commentContent = "This is a comment";

        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(commentEntityRepository.save(any())).thenReturn(mock(CommentEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.createComment(userName, postId, commentContent));
    }

    /*
     * 댓글 목록 조회 테스트 (페이지네이션 적용)
     */
    @Test
    @DisplayName("댓글 목록 조회 - 페이지네이션 적용")
    void testGetCommentsSuccess() {
        // Given
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        PostEntity mockPost = mock(PostEntity.class); // PostEntity 모킹
        UserEntity mockUser1 = mock(UserEntity.class);
        UserEntity mockUser2 = mock(UserEntity.class);

        List<CommentEntity> commentEntities = List.of(
                new CommentEntity(1L, "댓글 내용 1", mockPost, mockUser1),
                new CommentEntity(2L, "댓글 내용 2", mockPost, mockUser2)
        );

        Page<CommentEntity> commentPage = new PageImpl<>(commentEntities);

        // PostEntity 조회가 정상적으로 이루어지도록 모킹 (mockPost를 반환하도록 postEntityRepository 모킹 추가)
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        // CommentEntity 리스트 반환
        when(commentEntityRepository.findAllByPostId(eq(postId), any(Pageable.class)))
                .thenReturn(commentPage);

        // When
        Page<CommentEntity> result = postService.getComments(postId, pageable);

        // Then
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals("댓글 내용 1", result.getContent().get(0).getContent());
        Assertions.assertEquals("댓글 내용 2", result.getContent().get(1).getContent());
    }


    /*
     * 게시물 좋아요 실패 - 게시물 없음
     */
    @Test
    @DisplayName("게시물 좋아요 실패 - 게시물 없음")
    void testLikePostErrorPostNotFound() {
        String userName = "testUser";
        Long postId = 1L;

        // 유저는 존재하도록 Mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));

        // 게시물은 존재하지 않도록 설정
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.likePost(userName, postId));

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }


    /*
     * 게시물 좋아요 실패 - 유저 없음
     */
    @Test
    @DisplayName("게시물 좋아요 실패 - 유저 없음")
    void testLikePostErrorUserNotFound() {
        String userName = "testUser";
        Long postId = 1L;

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.likePost(userName, postId));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    /*
     * 댓글 작성 실패 - 게시물 없음
     */
    @Test
    @DisplayName("댓글 작성 실패 - 게시물 없음")
    void testCreateCommentErrorPostNotFound() {
        String userName = "testUser";
        Long postId = 1L;
        String commentContent = "This is a comment";

        //사용자 존재하도록 Mock 설정 추가
        when(userEntityRepository.findByUserName(userName))
                .thenReturn(Optional.of(new UserEntity()));

        //게시글 찾기 실패(Mock)
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.createComment(userName, postId, commentContent));

        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }


    /*
     * 댓글 작성 실패 - 유저 없음
     */
    @Test
    @DisplayName("댓글 작성 실패 - 유저 없음")
    void testCreateCommentErrorUserNotFound() {
        String userName = "testUser";
        Long postId = 1L;
        String commentContent = "This is a comment";

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> postService.createComment(userName, postId, commentContent));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }
}




