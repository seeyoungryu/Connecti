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
    회원가입
     */
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        User user = userService.join(request.getUserName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    /*
    로그인
     */
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getUserName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    /*
    인증된 사용자 정보를 반환
     */
    @GetMapping("/me")
    public Response<UserJoinResponse> me(Authentication authentication) {
        // me() 의 매개변수로 Authentication 객체를 선언 -> Spring Security가 자동으로 현재 인증된 사용자의 Authentication 객체를 주입
        // ㄴ *Authentication 객체* : 인증된 사용자 정보에 접근하기 위한 스프링 시큐리티의 도구 (Spring에서는 Authentication 객체를 << 컨트롤러 메서드 >> 에서 직접 주입받을 수 있음)

        User user = userService.loadUserByUsername(authentication.getName());
        //Authentication 객체는 Principal의 기본 정보를 가지고 있기 때문에, 이 객체에서 getName() 메서드를 호출하면 << 인증된 사용자>> 의 이름을 반환

        return Response.success(UserJoinResponse.fromUser(user));
    }
}