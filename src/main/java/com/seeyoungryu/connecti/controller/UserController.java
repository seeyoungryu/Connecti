package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;  // private final -> @RequiredArgsConstructor 필요

    @PostMapping
    //Todo : implement
    public void join() {
        //join
        userService.join("", "");
    }
}
