package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.response.Response;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostEntityRepository postEntityRepository;


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
    post 조회
     */

    public PostEntity getPost(Long postId) {
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));
    }


    /*
    post 삭제
     */
    @DeleteMapping("/{postId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable Long postId, Principal principal) {   //import java.security.Principal -> SecurityContext에서 인증된 사용자의 이름 가져오기
        String userName = principal.getName();

        postService.deletePost(userName, postId);

        return ResponseEntity.noContent().build();
    }


}




