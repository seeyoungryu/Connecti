package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.AlarmEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmEntityRepository alarmRepository;
    private final UserEntityRepository userEntityRepository;

    // 알람 생성 (게시글 좋아요, 댓글 작성 시)
    @Transactional
    public void createAlarm(UserEntity user, PostEntity post) {
        AlarmEntity alarm = AlarmEntity.of(user, post);
        alarmRepository.save(alarm);
    }

    // 특정 사용자의 알람 목록 조회 (페이징 적용)
    @Transactional
    public Page<AlarmEntity> getAlarms(String userName, Pageable pageable) {
        UserEntity user = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        return alarmRepository.findAllByUser(user, pageable);
    }

    // 알람 읽음 처리
    @Transactional
    public void markAlarmAsRead(Long alarmId, String userName) {
        AlarmEntity alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.ALARM_NOT_FOUND));

        if (!alarm.getUser().getUserName().equals(userName)) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        alarm.markAlarmAsRead();
        alarmRepository.save(alarm);
    }
}
