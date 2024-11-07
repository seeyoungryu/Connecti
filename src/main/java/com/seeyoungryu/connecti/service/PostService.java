package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;

    private final UserEntityRepository userEntityRepository;


    @Transactional
    public void createPost(String title, String body, String userName) {  //1.find user 2.save post 3.return
        // 1. 사용자 조회
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("username %s not found", userName)));

        // 2. 게시물 생성(PostEntity 생성) 및 저장 (of:정적 팩토리 메서드)
        PostEntity postEntity = PostEntity.of(title, body, userEntity);
        postEntityRepository.save(postEntity);
    }

    @Transactional
    public void deletePost(Long postId) {

    }

    public void deletePost(String userName, Long postId) {
    }

    public void modify(String userName, Long postId, String title, String body) {
    }

    public void my(String userName, Pageable mock) {
    }

    public PostEntity getPost(Long postId) {
        return null;
    }
}


