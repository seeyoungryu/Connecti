package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostEntityRepository PostEntityRepository;

    @Autowired
    public PostService(PostEntityRepository PostEntityRepository) {
        this.PostEntityRepository = PostEntityRepository;
    }

    @Transactional
    public PostEntity createPost(String title, String body) {
        if (title == null || body == null) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return PostEntityRepository.save(PostEntity.of(title, body));
    }

    @Transactional(readOnly = true)
    public PostEntity getPost(Long postId) {
        return PostEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional
    public void deletePost(Long postId) {
        PostEntity post = PostEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));
        PostEntityRepository.delete(post);
    }
}
