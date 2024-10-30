package com.seeyoungryu.connecti.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class UserJoinRequest {

    private String userName;
    private String password;

}


/* <DTO 클래스>
: 데이터 전송 객체: 요청받은 데이터를 전달받아 캡슐화하고, 서비스 계층에 전달하기 위해 사용

- @Getter 각 필드에 접근할 있게 함
- @AllArgsConstructor: 모든 필드를 포함한 생성자를 자동으로 생성

* <TDD> - 테스트에서 사용자 요청 데이터를 전달하는 형식을 정의했기 때문에,
이를 처리할(회원가입시 RequestBody 로 데이터 받아올 때 사용할 클래스 용도) 클래스를 만들어 테스트가 올바르게 작동하도록 하기 위함
(패키지 분류 - controller.request 로 쓰는것이 TDD 흐름을 강조하며 요청과 응답 객체를 명확히 구분하고 싶을 때 유용한 패턴임)

 */