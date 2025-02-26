package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.Post;
import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.model.entity.CommentEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.AlarmEntityRepository;
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
    private final AlarmEntityRepository alarmRepository;

    /**
     * 유저 이름으로 UserEntity 조회
     */
    private UserEntity findUserByName(String userName) {
        return userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(
                        ErrorCode.USER_NOT_FOUND,
                        String.format("username %s not found", userName)
                ));
    }

    /**
     * 게시글 ID로 PostEntity 조회
     */
    private PostEntity findPostById(Long postId) {
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new ConnectiApplicationException(
                        ErrorCode.POST_NOT_FOUND,
                        String.format("postId %s not found", postId)
                ));
    }

    /**
     * 소유권 검증 메서드
     *
     * @param owner      실제 소유자
     * @param requester  요청한 사용자
     * @param entityType 엔티티 종류 (Post, Comment)
     * @param entityId   엔티티 ID
     */
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

        return Post.fromEntity(postEntity);
    }

    @Transactional
    public void deletePost(String userName, Long postId) {
        UserEntity requester = findUserByName(userName);
        PostEntity postEntity = findPostById(postId);
        UserEntity owner = postEntity.getUser();

        validateOwnership(owner, requester, "Post", postId);
        postEntityRepository.delete(postEntity);
    }

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

    /**
     * 게시글 좋아요
     * 좋아요가 추가되면 게시글 작성자에게 알람 생성
     */
    @Transactional
    public boolean likePost(String username, Long postId) {
        UserEntity user = findUserByName(username);
        PostEntity post = findPostById(postId);

        boolean isLiked = post.likePost(user);
        if (isLiked) {
            createAlarm(post.getUser(), post);
        }
        return isLiked;
    }

    @Transactional
    public boolean unlikePost(String username, Long postId) {
        UserEntity user = findUserByName(username);
        PostEntity post = findPostById(postId);

        return post.unlikePost(user);
    }

    /**
     * 댓글 작성
     * 댓글이 추가되면 게시글 작성자에게 알람 생성
     */
    @Transactional
    public void createComment(String username, Long postId, String content) {
        UserEntity user = findUserByName(username);
        PostEntity post = findPostById(postId);

        CommentEntity comment = new CommentEntity(1L, content, post, user);
        commentEntityRepository.save(comment);

        createAlarm(post.getUser(), post);
    }

    @Transactional
    public Page<CommentEntity> getComments(Long postId, Pageable pageable) {
        findPostById(postId);
        return commentEntityRepository.findAllByPostId(postId, pageable);
    }

    /**
     * 알람 생성
     * 좋아요 또는 댓글이 등록될 때 게시글 작성자에게 알람 저장
     */
    private void createAlarm(UserEntity postOwner, PostEntity post) {
        AlarmEntity alarm = AlarmEntity.of(postOwner, post);
        alarmRepository.save(alarm);
    }
}
