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

//    @PostMapping
//    public ResponseEntity<void> createPost(@RequestBody PostCreateRequest request) {
//        PostEntity post = postService.createPost(request.getTitle(), request.getBody());
//        return Response.success(null);
//    }

    @PostMapping
    public ResponseEntity<Response<Void>> createPost(@RequestBody PostCreateRequest request, Authentication authentication) {
        // 인증된 사용자의 이름으로 포스트 작성
        postService.createPost(authentication.getName(), request.getTitle(), request.getBody());
        return ResponseEntity.ok(Response.success(null));
    }


//    @GetMapping("/{postId}")
//    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
//        PostEntity post = postService.getPost(postId);
//        return ResponseEntity.ok(new PostResponse(post));
//    }

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> getPost(@PathVariable Long postId) {
        PostEntity post = postService.getPost(postId);
        return ResponseEntity.ok(Response.success(new PostResponse(post)));
    }


//    @DeleteMapping("/{postId}")
//    @Secured("ROLE_USER")
//    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
//        postService.deletePost(postId);
//        return ResponseEntity.noContent().build();
//    }
//}

    @DeleteMapping("/{postId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}


/*
개선점: 보통 REST API에서는 POST /api/v1/posts 형태로 엔티티 생성 요청을 받는 것이 일반적입니다. 따라서 /create를 붙이지 않는 강사 코드가 RESTful API 설계에 더 적합합니다.
 */



/*
개선사항 요약
URL 경로를 RESTful 규칙에 맞게 /api/v1/posts로 단순화했습니다.
Authentication 객체를 사용하여 인증된 사용자의 이름을 가져와 포스트 작성 시 어떤 사용자가 작성했는지 확인할 수 있게 했습니다.
일관된 응답 포맷을 위해 ResponseEntity<Response<?>>를 반환하도록 변경했습니다.
권한 검사는 @Secured("ROLE_USER") 어노테이션을 유지하여, deletePost 메서드에 대해 특정 권한을 가진 사용자만 접근할 수 있도록 설정했습니다.
이렇게 수정된 코드로 인해, 내 코드가 더 RESTful하며, 인증된 사용자의 이름을 기반으로 작업을 수행할 수 있어 더욱 일관적이고 실용적인 API가 됩니다.
 */