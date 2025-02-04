package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.CommentCreateRequest;
import com.seeyoungryu.connecti.controller.response.CommentResponse;
import com.seeyoungryu.connecti.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<Void> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request,
            Authentication authentication
    ) {
        commentService.createComment(postId, authentication.getName(), request.getContent());
        return ResponseEntity.ok().build();
    }

    // 특정 게시물의 댓글 목록 조회 (페이지네이션 적용)
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long postId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getComments(postId, pageable));
    }
}
