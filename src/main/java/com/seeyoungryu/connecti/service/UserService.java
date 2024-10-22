package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //Todo : implement
    public User join(String username, String password) {
        //1. 입력한 username 으로 이미 가입된 user 가 있는지
        //2. 없으면 -> 회원가입 진행 (user를 DB에 등록)


        //반환 -> User 객체를 반환하도록 함
        return new User();
    }

    //Todo : implement
    public String login() { //반환 -> JWT사용할 것이므로 암호화된 문자열을 반환하는 메서드로 처리해야함 * -> 반환값 : String
        return "";
    }


}
