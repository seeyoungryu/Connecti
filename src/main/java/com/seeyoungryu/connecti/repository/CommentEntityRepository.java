package com.seeyoungryu.connecti.repository;

import com.seeyoungryu.connecti.model.entity.CommentEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {

    // 특정 게시물의 댓글을 페이지네이션하여 조회
    Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);

    Page<CommentEntity> findAllByPostId(Long postId, Pageable pageable);
}
