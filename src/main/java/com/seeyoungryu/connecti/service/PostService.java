package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Service
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Autowired
    public PostService(PostEntityRepository PostEntityRepository, UserEntityRepository userEntityRepository) {
        this.postEntityRepository = PostEntityRepository;
        this.userEntityRepository = userEntityRepository;
    }


    @Transactional
    public void createPost(String title, String body, String userName) {
        // @Todo : creatPost
        // 1. find user
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("$is not founded", userName)));     // todo :  이 코드 람다식 해석
        // 2. save post
        postEntityRepository.save(new PostEntity(1L, title, body));
        // return
    }

    @Transactional
    public void deletePost(Long postId) {
        PostEntity post = PostEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));
        PostEntityRepository.delete(post);
    }
}
