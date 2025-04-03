package com.seeyoungryu.connecti.model;

import lombok.Data;

@Data
public class AlarmArgs {

    private Long senderId;       // 알림을 발생시킨 사용자 ID
    private Long receiverId;     // 알림을 받는 사용자 ID

}
