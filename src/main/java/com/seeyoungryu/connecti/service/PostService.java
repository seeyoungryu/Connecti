package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    // 사용자 조회 로직 추출
    private UserEntity findUserByName(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("username %s not found", userName)));
    }

    // 게시물 존재 여부 확인 로직 추출
    private PostEntity findPostById(Long postId) {
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));
    }

    // 권한 확인 로직 추출
    private void validatePermission(UserEntity userEntity, PostEntity postEntity, String userName, Long postId) {
        if (!postEntity.getUser().equals(userEntity)) {
            throw new ConnectiApplicationException(
                    ErrorCode.INVALID_PERMISSION,
                    String.format("%s has no permission with %s", userName, postId)
            );
        }
    }

    @Transactional
    public void createPost(String title, String body, String userName) {
        UserEntity userEntity = findUserByName(userName);
        PostEntity postEntity = PostEntity.of(title, body, userEntity);
        postEntityRepository.save(postEntity);
    }

    @Transactional
    public void modify(String title, String body, String userName, Long postId) {
        UserEntity userEntity = findUserByName(userName);
        PostEntity postEntity = findPostById(postId);
        validatePermission(userEntity, postEntity, userName, postId);

        postEntity.setTitle(title);
        postEntity.setBody(body);
    }

    @Transactional
    public void deletePost(String userName, Long postId) {
        UserEntity userEntity = findUserByName(userName);
        PostEntity postEntity = findPostById(postId);
        validatePermission(userEntity, postEntity, userName, postId);

        postEntityRepository.delete(postEntity);
    }
}
