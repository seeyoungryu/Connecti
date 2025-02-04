package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import org.springframework.stereotype.Service;

@Service
public class PostLikeService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public PostLikeService(PostEntityRepository postEntityRepository, UserEntityRepository userEntityRepository) {
        this.postEntityRepository = postEntityRepository;
        this.userEntityRepository = userEntityRepository;
    }

    public void likePost(String username, Long postId) {
        UserEntity user = userEntityRepository.findByUserName(username)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        PostEntity post = postEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));

        post.likePost(user);
        postEntityRepository.save(post);
    }

    public void unlikePost(String username, Long postId) {
        UserEntity user = userEntityRepository.findByUserName(username)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        PostEntity post = postEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));

        post.unlikePost(user);
        postEntityRepository.save(post);
    }
}
