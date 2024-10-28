package com.seeyoungryu.connecti.controller.response;

import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.UserRole;
import lombok.AllArgsConstructor;

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
