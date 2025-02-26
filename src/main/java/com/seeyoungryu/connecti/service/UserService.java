package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.entity.AlarmEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.AlarmEntityRepository;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import com.seeyoungryu.connecti.service.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor //final
@Service
public class UserService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    private final AlarmEntityRepository alarmEntityRepository;

    private final JwtTokenUtils jwtTokenUtils;

    @Lazy
    private final PasswordEncoder encoder; // Bean 받아오기

    @Value("${jwt.secret-key}")  //application 설정값 자동 주입
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;


    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {  //리턴타입 : User
        return userEntityRepository.findByUserName(userName)  //사용자의 `username`을 기반으로 `User` 정보를 반환
                .map(User::fromEntity)    //이 메서드가 User 타입을 반환함
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("userName: %s", userName)));
    }

    /*
    회원가입
     */
    @Transactional //익셉션 발생시 엔티티 세이브 하는 부분이 롤백됨
    public User join(String userName, String password) {
        //1. 입력한 username 으로 이미 가입된 user 가 있는지 확인
        //(태스트 코드용 기본 코드 : Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);)
        userEntityRepository.findByUserName(userName).ifPresent(it -> {   // * it(=userEntity): 람다식(parameter -> {code})의 매개변수 이름을 간단히 표현
            throw new ConnectiApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName: %s", userName));
        });

        //(위에서 throw 가 안되고 넘어오면!)
        //2. 없으면 -> 회원가입 진행 (user를 DB에 등록)
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));  //비밀번호를 encode 해서 저장

        // 예외를 던지지 않고 User 객체 반환
        return User.fromEntity(userEntity);
        //throw new RuntimeException();  -> 예외처리 로직 테스트용
    }


    /*
    로그인
     */
    @Transactional
    public String login(String userName, String password) {
//        UserDetails userDetails = loadUserByUsername(userName);
//        //Spring Security가 사용자를 '인증'하므로 loadUserByUsername 호출 -> 사용자의 `username`을 기반으로 `User` 정보를 조회하여 반환함.
//회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));

        //비밀번호 체크
        if (!encoder.matches(password, userEntity.getPassword())) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        return jwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);
    }

    @Transactional
    public Page<AlarmEntity> getAlarms(String userName, Pageable pageable) {
        UserEntity user = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND));

        return alarmEntityRepository.findAllByUser(user, pageable);
    }

    @Transactional
    public void markAlarmAsRead(Long alarmId, String userName) {
        // 1. 알람 ID로 알람 조회
        AlarmEntity alarm = alarmEntityRepository.findById(alarmId)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.ALARM_NOT_FOUND));

        // 2. 현재 로그인한 사용자와 알람 소유자 확인
        if (!alarm.getUser().getUserName().equals(userName)) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        // 3. 알람을 읽음 처리
        alarm.markAlarmAsRead();
        alarmEntityRepository.save(alarm);  // 변경 사항을 DB에 반영
    }


}



