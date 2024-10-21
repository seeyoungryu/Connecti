package com.seeyoungryu.connecti.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc    //웹 서버를 띄우지 않고도 컨트롤러의 API를 테스트할 수 있음->API 요청을 "모의"로 보내고 그 응답을 검증하는 테스트 가능
public class UserControllerTest {
}
