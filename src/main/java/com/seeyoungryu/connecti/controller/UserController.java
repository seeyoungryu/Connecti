package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.UserJoinRequest;
import com.seeyoungryu.connecti.controller.request.UserLoginRequest;
import com.seeyoungryu.connecti.controller.response.Response;
import com.seeyoungryu.connecti.controller.response.UserJoinResponse;
import com.seeyoungryu.connecti.controller.response.UserLoginResponse;
import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /*
        Todo 로컬 히스토리 비교하며 수정 확인
     */
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        User user = userService.join(request.getUserName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getUserName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/me")
    public Response<UserJoinResponse> me(Authentication authentication) {
        User user = userService.loadUserByUsername(authentication.getName());
        return Response.success(UserJoinResponse.fromUser(user));
    }
}


/*
요약
/me 엔드포인트 추가: 강사 코드와 동일하게 @GetMapping("/me")를 추가하여 인증된 사용자 정보를 반환할 수 있습니다.
필드명 통일: UserJoinRequest에서 사용자명을 가져오는 메서드를 getName()으로 통일했습니다.
Authentication 객체 활용: me 메서드에서 현재 인증된 사용자의 정보를 가져오기 위해 Authentication 객체를 활용하였습니다.
이렇게 변경하면 강사 코드와 동일한 구조를 가지게 되며, 사용자 정보 조회 기능도 추가로 제공할 수 있습니다.

profile

 */




/* 컨트롤러 클래스
: 사용자의 회원가입,로그인 요청을 보낼 수 있는 API 엔드포인트를 정의하는 레이어
(사용자의 요청을 받아 <<서비스 레이어에서 필요한 작업을 호출>>하고, <<결과를 사용자에게 반환>>함)
-
<의존성 주입 관련>
1. private final 로 필드 선언하여 주입 - 해당 필드는 반드시 값이 할당된 상태로 유지되어야함, final 로 초기화 된 후 필드 변경 불가 ->안정성
2. @RequiredArgsConstructor - final 로 선언된 필드들을 자동으로 생성자의 파라미터로 받는 생성자를 만들어 필드에 값을 할당해줌
-> UserController 가 생성될 때 UserService 를 주입하여 의존성을 주입하는 방식이 됨 -> 이렇게 UserService 를 사용하게 됨.
--> 결론 :이 조합으로 인해 UserService 인스턴스가 확실하게 존재하며, 다른 곳에서 바뀌지 않는 안전한 의존성을 가진 UserController가 됨
 */