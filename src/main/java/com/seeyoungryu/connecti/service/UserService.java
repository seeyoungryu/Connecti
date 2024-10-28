package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;


    /*
    회원가입
     */

    //Todo : implement
    public User join(String userName, String password) {
        //     < 1. 입력한 username 으로 이미 가입된 user 가 있는지 확인 >
        //(태스트 코드용 기본 코드 : Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);)
        //(실제구현)
        userEntityRepository.findByUserName(userName).ifPresent(it -> {   // * it(userEntity라고 해도 됨): 람다식(parameter -> {code})의 매개변수 이름을 간단히 표현
            throw new ConnectiApplicationException();
        });

        //(위에서 throw 가 안되고 넘어오면!)
        //2. 없으면 -> 회원가입 진행 (user를 DB에 등록)
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, password));

        //반환 -> User 객체를 반환하도록 함
        return User.fromEntity(userEntity);
    }


    /*
    로그인
     */

    //Todo : implement
    public String login(String userName, String password) { //반환 -> JWT사용할 것이므로 암호화된 문자열을 반환하는 메서드로 처리해야함 * -> 반환값 : String

        //1. 회원가입 여부 확인
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(ConnectiApplicationException::new); // * 참고: lamda -> .orElseThrow(() -> new ConnectiApplicationException());)

        //2. 비밀번호 확인
        if (!userEntity.getPassword().equals(password)) {
            throw new ConnectiApplicationException();
        }

        //3. 토큰 생성

        return "";
    }
}
