package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.controller.response.CommentResponse;
import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.CommentEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.CommentEntityRepository;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentEntityRepository commentEntityRepository;
    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    // 댓글 작성
    @Transactional
    public void createComment(Long postId, String userName, String content) {
        UserEntity user = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        PostEntity post = postEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));

        commentEntityRepository.save(new CommentEntity(1L, content, post, user));
    }

    // 댓글 목록 조회 (페이지네이션 적용)
    public Page<CommentResponse> getComments(Long postId, Pageable pageable) {
        PostEntity post = postEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));

        return commentEntityRepository.findAllByPost(post, pageable)
                .map(CommentResponse::fromEntity);
    }
}
