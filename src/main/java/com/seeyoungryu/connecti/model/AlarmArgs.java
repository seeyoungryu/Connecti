package com.seeyoungryu.connecti.model;

import lombok.Data;

@Data
public class AlarmArgs {

    private Long senderId;    // 알림 발생시킨 사용자 (ex. 좋아요 누른 사람)
    private Long receiverId;  // 알림 받는 사용자 (ex. 게시글 주인)

    // 필요에 따라 필드 확장 가능
    // private String extraInfo;
}
