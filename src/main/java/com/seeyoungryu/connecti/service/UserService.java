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

        // 예외를 던지지 않고 User 객체 반환
        //return User.fromEntity(userEntity);
        throw new RuntimeException();
    }



        /* @Todo : 추후 주석 제거 필요
        { throw new RuntimeException(); 관련 }

        1. throw new RuntimeException();
        : 회원가입 메서드의 예외 상황을 더 잘 테스트하고, 예외 처리 로직을 확실히 이해하기 위한 학습 과정일 가능성 있음
         RuntimeException을 던짐으로써 일부러 에러 상황을 만들어, 애플리케이션이 이 에러를 어떻게 처리하는지를 보려고 하는듯
        + 예외가 발생할 때 서비스나 컨트롤러 레이어가 어떤 응답을 반환하는지"를 확인하려는 의도일 수 있음

        2. 리턴문이 없을 때의 동작 방식
        : User join(...) 메서드의 반환 타입이 void 가 아니고 User라면, 정상적으로 User 객체를 반환해야 함
        하지만 이 경우, RuntimeException을 던지면 메서드가 정상적으로 종료되지 않고 예외가 발생하며 종료됨
        따라서, 메서드 내부에서 throw new RuntimeException();이 실행되면 해당 메서드는 즉시 종료되며
        , 호출한 쪽에서는 User 객체를 반환받지 않고 예외를 받게 됨 -> 리턴문이 필요 없게 되며, 메서드는 예외와 함께 종료

        ( 참고 -> 대신 예외가 던져져 호출한 쪽(예: 컨트롤러)으로 전달됨
        컨트롤러나 전역 예외 처리 클래스는 이 예외를 받아서 적절한 응답을 사용자에게 반환하도록 처리함.
        그래서 예외가 발생한 경우에는 반환 타입과 상관없이 호출한 쪽에서 예외를 받는 흐름으로 동작하게 됩
        */


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