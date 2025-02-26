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

    // 알람 목록 조회
    @Transactional
    public Page<AlarmEntity> getAlarms(String userName, Pageable pageable) {
        UserEntity user = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        return alarmRepository.findAllByUser(user, pageable);
    }


    /*
    alarmRepository.findById(alarmId) → ID로 알람을 찾음.
찾은 알람이 현재 로그인한 사용자(userName)의 것인지 확인.
만약 다른 사용자의 알람이면 예외 발생(INVALID_PERMISSION).
alarm.markAsRead() 실행 → 알람을 읽음 처리.
alarmRepository.save(alarm) → 변경 사항을 DB에 저장.
     */
    @Transactional
    public void markAlarmAsRead(Long alarmId, String userName) {
        AlarmEntity alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.ALARM_NOT_FOUND));

        if (!alarm.getUser().getUserName().equals(userName)) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        alarm.markAlarmAsRead();
        alarmRepository.save(alarm);  // 변경 사항을 DB에 반영
    }


}


/*
알람 저장: createAlarm() → 게시글 좋아요/댓글 등록 시 실행
알람 조회: getAlarms() → 특정 유저의 알람을 페이징하여 반환
 */