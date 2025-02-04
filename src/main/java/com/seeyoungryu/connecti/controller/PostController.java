package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.CommentCreateRequest;
import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.request.PostModifyRequest;
import com.seeyoungryu.connecti.controller.response.CommentResponse;
import com.seeyoungryu.connecti.controller.response.PostResponse;
import com.seeyoungryu.connecti.controller.response.Response;
import com.seeyoungryu.connecti.model.Post;
import com.seeyoungryu.connecti.service.CommentService;
import com.seeyoungryu.connecti.service.PostLikeService;
import com.seeyoungryu.connecti.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;


    /*
    post 작성 성공
     */
    @PostMapping
    public ResponseEntity<Response<Void>> createPost(@RequestBody PostCreateRequest request, Authentication authentication) { // Todo 앞에서 context 했던 정보를 넣어줄 수 있다가 뭔소리?
        // 인증된 사용자의 이름으로 포스트 작성
        postService.createPost(request.getTitle(), request.getBody(), authentication.getName());
        return ResponseEntity.ok(Response.success(null));
    }

    /*
    포스트 수정
     */
    @PutMapping("/{postId}")
    public Response<PostResponse> modifyPost(@PathVariable Long postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        // postService.modifyPost 메서드 호출 및 Post 객체 반환
        Post modifiedPost = postService.modifyPost(request.getTitle(), request.getBody(), authentication.getName(), postId);

        // Post 객체를 PostResponse로 변환
        PostResponse postResponse = PostResponse.fromPost(modifiedPost);

        // 변환된 PostResponse를 Response.success로 감싸서 반환
        return Response.success(postResponse);
    }


    /*
    post 삭제
     */
    @DeleteMapping("/{postId}")
    @Secured("ROLE_USER")
    public Response<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
        postService.deletePost(authentication.getName(), postId);
        return Response.success(null);
    }


    /*
    피드 조회
     */
    @GetMapping
    public Response<Page<PostResponse>> getFeedlist(Pageable pageable, Authentication authentication) {  // 페이징 처리 ~ 리스트 형태의 API 에서는 페이징이 필요함.
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    /*
 내 피드 조회
  */
    @GetMapping("/myFeedList")
    public Response<Page<PostResponse>> getMyFeedList(Pageable pageable, Authentication authentication) {
        return Response.success(postService.myList(authentication.getName(), pageable).map(PostResponse::fromPost));
    }


    /*
    좋아요 기능
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, Authentication authentication) {
        postLikeService.likePost(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, Authentication authentication) {
        postLikeService.unlikePost(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }


    /*
    댓글 기능
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest request, Authentication authentication) {
        commentService.createComment(postId, authentication.getName(), request.getContent());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(@PathVariable Long postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getComments(postId, pageable));
    }


}




