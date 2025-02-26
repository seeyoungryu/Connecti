package com.seeyoungryu.connecti.controller;

import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public ResponseEntity<Page<AlarmEntity>> getAlarms(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        String userName = userDetails.getUsername();
        return ResponseEntity.ok(alarmService.getAlarms(userName, pageable));
    }
}

/*
/api/v1/alarms 엔드포인트에서 유저의 알람을 조회
페이징 처리 적용
로그인한 유저만 조회 가능 (@AuthenticationPrincipal 활용)
 */
