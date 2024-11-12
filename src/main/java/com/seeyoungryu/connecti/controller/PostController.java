package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.response.PostResponse;
import com.seeyoungryu.connecti.controller.response.Response;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;


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
     <`createPost` 메서드 > : 인증된 사용자의 정보를 기반으로 `postService`를 통해 게시물을 생성한 뒤 성공 응답(새로운 게시물 작성)을 반환함

    1. @RequestBody
    @RequestBody PostCreateRequest request: 요청의 본문(body)에 있는 JSON 데이터를 `PostCreateRequest` 객체로 변환하여 받음
    여기에는 게시물의 `title`과 `body` 같은 필드가 포함되어 있어야 함

    2. Authentication authentication
    Spring Security에서 제공하는 객체로, 현재 '인증된' 사용자의 정보를 있음
    여기서는 `authentication.getName()`을 통해 현재 사용자의 이름(또는 아이디)을 가져옴
*/


    /*
    post 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> getPost(@PathVariable Long postId) {
        PostEntity post = postService.getPost(postId);
        return ResponseEntity.ok(Response.success(new PostResponse(post)));
    }


    /*
    post 삭제
     */
    @DeleteMapping("/{postId}")
    @Secured("ROLE_USER")                           //deletePost 메서드에 대해 특정 권한을 가진 사용자만 접근할 수 있도록 설정
    public ResponseEntity<Response<Void>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}


    /* Response<Void> : 데이터가 없는 성공 응답을 반환
    ResponseEntity<Response<?>> 형태로 반환하도록 변경 ~ 이유: API 응답의 포맷을 표준화
    전체 API가 일관된 포맷을 가질 때, 클라이언트가 응답을 예측하고 처리하기 쉬워지기 때문임
    (예를 들어, 응답 형태가 항상 Response 래퍼를 통해 전달된다면,
    클라이언트는 모든 응답이 data, status, message와 같은 공통적인 필드를 가질 것이라고 예상할 수 있음)
     */



