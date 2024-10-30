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
일반적으로 모든 필드에 게터를 추가하여 JSON 응답을 정확하게 반환하도록 하는 것이 좋음. (직렬화 관련)
 */

/*
- UserJoinResponse가 성공 결과에 해당하는 이유:
1. 응답 데이터의 역할: UserJoinResponse는 주로 회원가입이 성공했을 때만 클라이언트에 반환됩니다.
따라서 에러 상황이나 실패 결과는 이 클래스와 무관하게 처리됩니다.
2. 사용자 정보 캡슐화: UserJoinResponse는 사용자 정보 <<일부>>만 포함하고 있으며,
비밀번호 등 민감한 정보는 포함되지 않습니다.
이는 회원가입 성공 후 사용자 확인을 위해 필요한 <<최소한의 정보만 클라이언트에 반환하는 목적>>을 가집니다.
--------------------------------------------------------------------------------
- fromUser() 의 역할(*정적 팩토리 메서드(객체를 직접 생성하지 않고 필요한 정보를 받아서 새 객체를 만들어 주는 메서드))
: 회원가입 '성공' 후 사용자 정보가 담긴 User 객체를 받아서, 이 정보를 그대로 클라이언트에 보내지 않고,
필요한 정보(예: id, userName, role)만 담아 응답용 객체인 UserJoinResponse 객체를 만들어서 보냄
->  UserJoinResponse는 회원가입이 성공했다는 결과와 함께,필요한 정보만 안전하게 반환함
 */


