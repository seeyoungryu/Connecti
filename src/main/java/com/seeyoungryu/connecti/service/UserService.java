package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder encoder; // Bean 받아오기
    // final 필드 -> @RequiredArgsConstructor 붙임


    /*
    회원가입
     */
    @Transactional
    public User join(String userName, String password) {
        //1. 입력한 username 으로 이미 가입된 user 가 있는지 확인
        //(태스트 코드용 기본 코드 : Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);)
        userEntityRepository.findByUserName(userName).ifPresent(it -> {   // * it(userEntity라고 해도 됨): 람다식(parameter -> {code})의 매개변수 이름을 간단히 표현
            throw new ConnectiApplicationException(ErrorCode.DUPLICATE_USER_NAME, String.format("%s is duplicated", userName));
        });

        //(위에서 throw 가 안되고 넘어오면!)
        //2. 없으면 -> 회원가입 진행 (user를 DB에 등록)
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));  //비밀번호를 encode 해서 저장

        throw new ConnectiApplicationException(ErrorCode.DUPLICATE_USER_NAME, String.format("%s is duplicated", userName));
        //반환 -> User 객체를 반환하도록 함
        //return User.fromEntity(userEntity);
    }


    /*
    로그인
     */
    @Transactional
    public String login(String userName, String password) { //반환값 -> JWT사용할 것이므로 암호화된 문자열을 반환하는 메서드로 처리해야함 * -> String

        //1. 회원가입 여부 확인
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new ConnectiApplicationException(ErrorCode.DUPLICATE_USER_NAME, ""));

        //2. 비밀번호 확인
        if (!userEntity.getPassword().equals(password)) {
            throw new ConnectiApplicationException(ErrorCode.DUPLICATE_USER_NAME, "");
        }

        //3. 토큰 생성 @Todo
        return "";
    }
}





/*
( @Todo 이 주석 정리 필요함


람다식 :  (parameter) -> { code } 형식
참고 람다식 -> :: (참조형)
//UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(ConnectiApplicationException::new);
- lamda -> .orElseThrow(() -> new ConnectiApplicationException());)


- @Transactional : 익셉션 발생시 엔티티 세이브 하는 부분이 롤백됨 (작업 실패의 경우 롤백하도록 설정->데이터 무결성 보장)

 */