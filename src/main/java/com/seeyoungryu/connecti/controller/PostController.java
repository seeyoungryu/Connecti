package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.CommentCreateRequest;
import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.request.PostModifyRequest;
import com.seeyoungryu.connecti.controller.response.ApiResponse;
import com.seeyoungryu.connecti.controller.response.CommentResponse;
import com.seeyoungryu.connecti.controller.response.PostResponse;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.Post;
import com.seeyoungryu.connecti.service.CommentService;
import com.seeyoungryu.connecti.service.PostLikeService;
import com.seeyoungryu.connecti.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;


    /*
    post 작성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody PostCreateRequest request, Authentication authentication) {

        // authentication이 null인지 확인 예외처리 추가
        if (authentication == null || authentication.getName() == null) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        // 로그인된 사용자의 이름으로 포스트 작성
        postService.createPost(request.getTitle(), request.getBody(), authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /*
    포스트 수정
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> modifyPost(@PathVariable Long postId,
                                                                @RequestBody PostModifyRequest request,
                                                                Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        Post modifiedPost = postService.modifyPost(request.getTitle(), request.getBody(), authentication.getName(), postId);

        log.debug("modifiedPost: {}", modifiedPost); //반환값 확인


        //Null 체크 추가  -> 로깅 확인 가능
        if (modifiedPost == null) {
            log.error("modifyPost() returned null for postId: {}", postId);
            throw new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND);
        }

        return ResponseEntity.ok(ApiResponse.success(PostResponse.fromPost(modifiedPost)));
    }


    /*
    post 삭제
     */
    //Spring Security , Enum : ROLE_USER -> Todo : @Secured("ROLE_USER")를 메서드 단위가 아닌 서비스 계층에서 적용하는 것이 더 유연함? 수정필요?
    @DeleteMapping("/{postId}")
    @Secured("ROLE_USER")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        postService.deletePost(authentication.getName(), postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null)); // 상태 코드 수정 (204 No Content)
    }


    /*
    피드 조회
     */
    @GetMapping
    public ApiResponse<Page<PostResponse>> getFeedlist(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // JPA의 자동 페이지네이션 ~ 리스트 형태의 API 에서는 페이징이 필요함
        // 정렬기준(최신순), 기본 페이지 크기 = 10 (Spring 기본값 = 20)
        return ApiResponse.success(postService.list(pageable).map(PostResponse::fromPost));
    }


    /*
    내 피드 조회
     */
    @GetMapping("/myFeedList")
    public ApiResponse<Page<PostResponse>> getMyFeedList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        return ApiResponse.success(postService.myList(authentication.getName(), pageable).map(PostResponse::fromPost));
    }


    /*
    좋아요 기능
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ConnectiApplicationException(ErrorCode.ALREADY_LIKED);
        }

        postLikeService.likePost(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, Authentication authentication) {
        postLikeService.unlikePost(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    /*
    댓글 기능
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request,
            Authentication authentication
    ) {
        //인증 여부 확인
        if (authentication == null || authentication.getName() == null) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        //댓글 내용 유효성 검사
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_COMMENT);
        }

        // * 게시글 존재 여부 확인  Todo → 이건 `commentService.createComment()`에서 내부적으로 예외 발생 가능?
        commentService.createComment(postId, authentication.getName(), request.getContent());

        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @PathVariable Long postId,
            Pageable pageable
    ) {
        //게시글 존재 여부 확인 Todo : commentService 내부에서 처리 가능?
        Page<CommentResponse> comments = commentService.getComments(postId, pageable);

        //댓글이 없는 경우 예외는 필요 없으나, 클라이언트가 알 수 있도록 로그 추가함
        if (comments.isEmpty()) {
            log.info("No comments found for postId: {}", postId);
        }

        return ResponseEntity.ok(ApiResponse.success(comments));
    }


}




