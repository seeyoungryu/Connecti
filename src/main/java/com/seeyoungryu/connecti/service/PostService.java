package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.Post;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.PostEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    public PostEntity createPost(String title, String body, String userName) {
        UserEntity userEntity = findUserByName(userName);
        PostEntity postEntity = PostEntity.of(title, body, userEntity);
        return postEntityRepository.save(postEntity);
    }


    @Transactional
    public Post modifyPost(String title, String body, String userName, Long postId) {
        // 유저와 게시글 조회
        UserEntity userEntity = findUserByName(userName);
        PostEntity postEntity = findPostById(postId);

        // 권한 검증
        validatePermission(userEntity, postEntity, userName, postId);

        // 게시글 수정
        postEntity.setTitle(title);
        postEntity.setBody(body);

        // 수정된 엔티티 저장 및 반환
        PostEntity updatedPostEntity = postEntityRepository.saveAndFlush(postEntity);
        return Post.fromEntity(updatedPostEntity);
    }


    @Transactional
    public void deletePost(String userName, Long postId) {
        UserEntity userEntity = findUserByName(userName);
        PostEntity postEntity = findPostById(postId);
        validatePermission(userEntity, postEntity, userName, postId);

        postEntityRepository.delete(postEntity);
    }

    @Transactional
    public Page<Post> list(Pageable pageable) { //엔티티는 서비스단에서 반환할때 사용 안하기로..?
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }


    @Transactional
    public Page<Post> myList(String userName, Pageable pageable) {
        UserEntity userEntity = findUserByName(userName);

        return postEntityRepository.findAllByUserId(userEntity.getId(), pageable)
                .map(Post::fromEntity);
    }

}

