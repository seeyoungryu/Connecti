package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.response.PostResponse;
import com.seeyoungryu.connecti.controller.response.Response;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping
    public ResponseEntity<Response<Void>> createPost(@RequestBody PostCreateRequest request, Authentication authentication) {
        // 인증된 사용자의 이름으로 포스트 작성
        postService.createPost(authentication.getName(), request.getTitle(), request.getBody());
        return ResponseEntity.ok(Response.success(null));
    }


    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> getPost(@PathVariable Long postId) {
        PostEntity post = postService.getPost(postId);
        return ResponseEntity.ok(Response.success(new PostResponse(post)));
    }


    @DeleteMapping("/{postId}")
    @Secured("ROLE_USER")                           //deletePost 메서드에 대해 특정 권한을 가진 사용자만 접근할 수 있도록 설정
    public ResponseEntity<Response<Void>> deletePost(@PathVariable Long postId) {       // Todo 일관된 응답 포맷을 위해 ResponseEntity<Response<?>>를 반환하도록 변경 ?
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}



