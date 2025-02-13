package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.Post;
import com.seeyoungryu.connecti.model.entity.CommentEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.CommentEntityRepository;
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
    private final CommentEntityRepository commentEntityRepository;

    /*
    공통 유틸 메서드 (중복 로직 분리)
     */
    private UserEntity findUserByName(String userName) {
        return userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(
                        ErrorCode.USER_NOT_FOUND,
                        String.format("username %s not found", userName)
                ));
    }


    private PostEntity findPostById(Long postId) {
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(
                        ErrorCode.POST_NOT_FOUND,
                        String.format("%s not found", postId)
                ));
    }


    //    private void validatePermission(UserEntity userEntity, PostEntity postEntity, String userName, Long postId) {
    //        if (!postEntity.getUser().equals(userEntity)) {
    //            throw new ConnectiApplicationException(
    //                    ErrorCode.INVALID_PERMISSION,
    //                    String.format("%s has no permission with %s", userName, postId)
    //            );
    //        }
    //    }

    // validateOwnership 방식으로 개선 (범용적으로 활용 가능) -> 게시글(Post), 댓글(Comment) 모두에서 재사용 가능
    private void validateOwnership(UserEntity owner, UserEntity requester, String entityType, Long entityId) {
        if (!owner.equals(requester)) {
            throw new ConnectiApplicationException(
                    ErrorCode.INVALID_PERMISSION,
                    String.format("%s has no permission to modify/delete %s with ID %d", requester.getUserName(), entityType, entityId)
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
        UserEntity requester = findUserByName(userName);
        PostEntity postEntity = findPostById(postId);

        if (postEntity == null) {
            log.error("findPostById({}) returned null!", postId);
            throw new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND);
        }

        UserEntity owner = postEntity.getUser();

        validateOwnership(owner, requester, "Post", postId);

        postEntity.updateTitle(title);
        postEntity.updateBody(body);

        Post modifiedPost = Post.fromEntity(postEntity);

        log.debug("Modified Post: {}", modifiedPost);

        return modifiedPost;
    }


    @Transactional
    public void deletePost(String userName, Long postId) {
        UserEntity requester = findUserByName(userName);
        PostEntity postEntity = findPostById(postId);
        UserEntity owner = postEntity.getUser();

        validateOwnership(owner, requester, "Post", postId);

        postEntityRepository.delete(postEntity);
    }


    //엔티티가 아닌 DTO 반환 (Page<Post>)
    @Transactional
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    @Transactional
    public Page<Post> myList(String userName, Pageable pageable) {
        UserEntity userEntity = findUserByName(userName);
        return postEntityRepository.findAllByUserId(userEntity.getId(), pageable)
                .map(Post::fromEntity);
    }

    @Transactional
    public boolean likePost(String username, Long postId) {
        UserEntity user = findUserByName(username);
        PostEntity post = findPostById(postId);

        return post.likePost(user);
    }

    @Transactional
    public boolean unlikePost(String username, Long postId) {
        UserEntity user = findUserByName(username);
        PostEntity post = findPostById(postId);

        return post.unlikePost(user);
    }

    @Transactional
    public void createComment(String username, Long postId, String content) {
        UserEntity user = findUserByName(username);
        PostEntity post = findPostById(postId);

        CommentEntity comment = new CommentEntity(1L, content, post, user);
        commentEntityRepository.save(comment);
    }

    //Todo 댓글 목록 조회 시 엔티티가 아닌 DTO 반환하도록 수정해야하는지?
    @Transactional
    public Page<CommentEntity> getComments(Long postId, Pageable pageable) {
        findPostById(postId);
        return commentEntityRepository.findAllByPostId(postId, pageable);
    }
}
