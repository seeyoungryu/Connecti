package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

//회원가입의 '성공' 결과를 캡슐화 하는 클래스
@Getter
@AllArgsConstructor
public class UserJoinResponse {
    private Long id;
    private String userName;
    private UserRole role;

    //User 객체를 기반으로 회원가입 응답을 생성하는 메서드
    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUserName(),
                user.getRole());
    }
}


/* `UserJoinResponse`와 같이 외부에 노출되는 DTO에서는
일반적으로 모든 필드에 게터를 추가하여 JSON 응답을 정확하게 반환하도록 하는 것이 좋음.
 */
