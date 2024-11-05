package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.PostCreateRequest;
import com.seeyoungryu.connecti.controller.response.PostResponse;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostEntity post = postService.createPost(request.getTitle(), request.getBody());
        return ResponseEntity.ok(new PostResponse(post));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostEntity post = postService.getPost(postId);
        return ResponseEntity.ok(new PostResponse(post));
    }

    @DeleteMapping("/{postId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
