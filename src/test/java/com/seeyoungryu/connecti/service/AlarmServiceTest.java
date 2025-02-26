package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.AlarmRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AlarmServiceTest {

    @Autowired
    private AlarmService alarmService;

    @MockBean
    private AlarmRepository alarmRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    @DisplayName("알람 생성 성공")
    void createAlarm_Success() {
        UserEntity user = new UserEntity("user1", "password");
        PostEntity post = new PostEntity("Test Title", "Test Content", user);

        AlarmEntity alarm = AlarmEntity.of(user, post);

        when(alarmRepository.save(any())).thenReturn(alarm);

        Assertions.assertDoesNotThrow(() -> alarmService.createAlarm(user, post));
    }

    @Test
    @DisplayName("알람 조회 실패 - 유저 없음")
    void getAlarms_ErrorUserNotFound() {
        when(userEntityRepository.findByUserName("unknownUser")).thenReturn(Optional.empty());

        ConnectiApplicationException e = Assertions.assertThrows(ConnectiApplicationException.class,
                () -> alarmService.getAlarms("unknownUser", PageRequest.of(0, 10)));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }
}


