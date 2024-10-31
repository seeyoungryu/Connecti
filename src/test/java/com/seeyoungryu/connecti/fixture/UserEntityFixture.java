package com.seeyoungryu.connecti.fixture;

import com.seeyoungryu.connecti.model.entity.UserEntity;


/* < Fixture 클래스 사용 >
테스트에서 사용될 더미(가짜) UserEntity 데이터를 생성하여,
 반복적인 코드 작성을 줄이고 테스트의 가독성을 높이기 위함

- Mock 객체와 Fixture의 차이점
Mock은 메서드가 호출될 때 특정 동작을 흉내 내도록 설정하는 데 유리함.
Fixture는 실제 사용될 가짜 객체를 만들어 필요한 데이터를 그대로 제공하기 때문에 실제 객체처럼 동작

- UserEntityFixture.get을 통해 만들어진 데이터는 DB에 영구적으로 남지 않음
이는 주로 테스트 환경에서만 사용되는 일시적인 데이터임

- Mock 말고 fixture 사용하면?
Fixture는 실제 UserEntity와 동일한 속성을 가지며 정상적인 객체와 유사한 방식으로 동작함
반면 Mock은 원하는 동작만 설정해서 더 구체적인 테스트가 가능하므로 코드에 따라 다르게 사용
 */


public class UserEntityFixture {
    public static UserEntity get(String userName, String password) {

        UserEntity result = new UserEntity();
        result.setId(1L);
        result.setUserName(userName);
        result.setPassword(password);


        return new UserEntity();
    }
}

