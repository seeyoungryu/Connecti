package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
        postEntityRepository.save(postEntity);  //코드 스타일 다르게 ~객체가 즉시 저장된다는 의미의 느낌으로 -> PostEntity postEntity = postEntityRepository.save(PostEntity.of(title, body, userEntity));

        return;
    }

    @Transactional
    public void modify(String title, String body, String userName, Long postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("username %s not found", userName)));

        // post 존재여부 확인
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));

        // 포스트 permission 확인
        if (!postEntity.getUser().equals(userEntity)) {
            throw new ConnectiApplicationException(
                    ErrorCode.INVALID_PERMISSION,
                    String.format("%s has no permission with %s", userName, postId)
            );
        }

        // 수정 내용 반영
        postEntity.setTitle(title);
        postEntity.setBody(body);
    }


    @Transactional
    public void deletePost(String userName, Long postId) {

        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("username %s not found", userName)));


        // post 존재여부 확인
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));

        // 포스트 permission 확인
        if (!postEntity.getUser().equals(userEntity)) {
            throw new ConnectiApplicationException(
                    ErrorCode.INVALID_PERMISSION,
                    String.format("%s has no permission with %s", userName, postId)
            );
        }

        postEntityRepository.delete(postEntity);
    }


}


