package com.seeyoungryu.connecti.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // JSON 역직렬화를 위해 -> 기본 생성자 유지
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 추가 (불변성 유지)
public class PostCreateRequest {
    private String title;  //final 제거 (기본 생성자에서 값 할당을 위해 필요)
    private String body;
}

