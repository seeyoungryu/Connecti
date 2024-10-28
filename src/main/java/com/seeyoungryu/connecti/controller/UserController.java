package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.controller.request.UserJoinRequest;
import com.seeyoungryu.connecti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;  // private final -> @RequiredArgsConstructor 필요

    @PostMapping("/join")
    //Todo : implement
    public void join(@RequestBody UserJoinRequest request) {
        //join
        userService.join(request.getUserName(), request.getPassword()); //바디로 받아온값을 실제 메서드에 넘겨줄 수 있도록
    }
}
