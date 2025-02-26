package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.AlarmEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
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
    public void createAlarm(UserEntity user, PostEntity post) {
        AlarmEntity alarm = AlarmEntity.of(user, post);
        alarmRepository.save(alarm);
    }

    // 알람 목록 조회
    public Page<AlarmEntity> getAlarms(String userName, Pageable pageable) {
        UserEntity user = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        return alarmRepository.findAllByUser(user, pageable);
    }
}


/*
알람 저장: createAlarm() → 게시글 좋아요/댓글 등록 시 실행
알람 조회: getAlarms() → 특정 유저의 알람을 페이징하여 반환
 */