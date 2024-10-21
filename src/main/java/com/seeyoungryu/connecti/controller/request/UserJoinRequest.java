package com.seeyoungryu.connecti.controller.request;

/* <TDD> 관련
  UserJoinRequest 클래스의 의도 -> 회원가입시 RequestBody 로 데이터 받아올 때 사용할 클래스 용도
- 데이터 전달 객체 (DTO): UserJoinRequest는 주로 사용자로부터 전달받은 데이터(회원가입 요청의 사용자명, 비밀번호 등)를 캡슐화하는 DTO 역할을 함.
    ㄴ> DTO.UserRequestDto.java 와 같은 역할 ~ 데이터 전달 객체 / <-> controller.request 로 쓰는것이 TDD 흐름을 강조하며 요청과 응답 객체를 명확히 구분하고 싶을 때 유용한 패턴이라고 볼 수 있음.
- TDD에서의 사용: 테스트에서 사용자 요청 데이터를 전달하는 형식을 정의했기 때문에, 이를 처리할 클래스를 만들어 테스트가 올바르게 작동하도록 하기 위함.
 */

public class UserJoinRequest {

    private String userName;
    private String password;


}
