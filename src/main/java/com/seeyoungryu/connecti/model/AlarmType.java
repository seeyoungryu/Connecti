package com.seeyoungryu.connecti.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

    NEW_COMMENT_ON_POST("new comment!"),            //타입별 알람메세지 클라이언트가 아닌 서버에서 관리 (메세지 변화 가능성 있으므로) , db 에서 관리 하지 않음
    NEW_LIKE_ON_POST("new like!");

    private final String alarmText;


}


