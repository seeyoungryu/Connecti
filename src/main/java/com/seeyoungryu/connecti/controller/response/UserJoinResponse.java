package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

//회원가입의 성공 결과를 캡슐화 하는 클래스(회원가입이 성공했을 때만 ~> 클라이언트에 반환됨)
@Getter
@AllArgsConstructor
public class UserJoinResponse {
    private Long id;
    private String userName;
    private UserRole role;

    //User 객체를 기반으로 UserJoinResponse(회원가입 응답)을 생성, 반환하는 메서드
    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUsername(),
                user.getRole());
    }
}
