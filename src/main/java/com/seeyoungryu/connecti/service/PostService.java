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

//    @Autowired
//    public PostService(PostEntityRepository PostEntityRepository, UserEntityRepository userEntityRepository) {
//        this.postEntityRepository = PostEntityRepository;
//        this.userEntityRepository = userEntityRepository;
//    }



    /*
의존성 관리

내 코드: @Autowired를 사용하여 PostService에 의존성을 주입합니다.
강사 코드: @RequiredArgsConstructor로 필드를 final로 선언하고 의존성을 주입하여, 객체의 불변성을 보장하고 테스트 및 유지보수가 용이합니다.
개선: @RequiredArgsConstructor를 사용하여 생성자 주입을 일관성 있게 적용하면, 생성자에 의한 주입이 강제되므로 테스트 시 mock 주입도 더 편리합니다.

     */

//
//    @Transactional
//    public void createPost(String title, String body, String userName) {
//        // @Todo : creatPost
//        // 1. find user
//        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
//                new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("$is not founded", userName)));     // todo :  이 코드 람다식 해석
//        // 2. save post
//        postEntityRepository.save(new PostEntity(1L, title, body));
//        // return
//    }
//
//    @Transactional
//    public void deletePost(Long postId) {
//        PostEntity post = PostEntityRepository.findById(postId)
//                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.POST_NOT_FOUND));
//        PostEntityRepository.delete(post);
//    }

    @Transactional
    public void createPost(String title, String body, String userName) {
        // 1. 사용자 조회
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND,
                        String.format("User with username %s not found", userName)));

        // 2. 게시물 생성 및 저장
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
}



/*
코드 설명
@RequiredArgsConstructor로 생성자 주입: 클래스 필드를 final로 선언하고 @RequiredArgsConstructor를 사용하여, 생성자 주입을 간결하게 만듭니다.
정적 팩토리 메서드 PostEntity.of() 사용: PostEntity 생성 시 PostEntity.of(title, body, userEntity)로 생성하여, 생성 방식에 일관성을 제공합니다.
예외 메시지 수정: 예외 메시지에 구체적인 정보를 추가하여, 오류 발생 시 디버깅에 도움이 되도록 구성하였습니다.
ErrorCode: 에러 코드와 메시지를 통일성 있게 사용하여, 문제 발생 시 오류 코드를 통해 빠르게 원인을 파악할 수 있습니다.

 */