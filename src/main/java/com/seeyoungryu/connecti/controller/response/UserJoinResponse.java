package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponse {
    private Long id;
    private String userName;
    private UserRole role;

    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUserName(),
                user.getRole());
    }
}


/* @Getter 추가 관련 (데이터 직렬화)
`UserJoinResponse`에 게터를 추가하는 것은 **JSON 직렬화**와 관련이 있습니다.
Spring Boot에서 응답 객체를 JSON으로 변환할 때, 각 필드에 접근하기 위해 게터가 필요합니다.
`@Getter`나 직접 작성한 게터가 없으면 Jackson이 필드 값을 직렬화할 수 없어, 클라이언트는 응답 값이 빈 객체로 받게 됩니다.

게터를 추가함으로써:
1. **응답 데이터 접근 가능**: `UserJoinResponse` 객체가 JSON으로 변환될 때 필드 값이 노출되어 응답으로 전달됩니다.
2. **유지 보수성 향상**: 다른 클래스나 테스트에서 해당 객체의 필드 값을 확인하거나 검증할 때 유용합니다.

따라서, `UserJoinResponse`와 같이 외부에 노출되는 DTO에서는 일반적으로 모든 필드에 게터를 추가하여 JSON 응답을 정확하게 반환하도록 하는 것이 좋습니다.
 */
