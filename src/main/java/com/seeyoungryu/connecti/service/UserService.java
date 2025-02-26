package com.seeyoungryu.connecti.service;

import com.seeyoungryu.connecti.exception.ConnectiApplicationException;
import com.seeyoungryu.connecti.exception.ErrorCode;
import com.seeyoungryu.connecti.model.User;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import com.seeyoungryu.connecti.repository.UserEntityRepository;
import com.seeyoungryu.connecti.service.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Lazy
    private final PasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userEntityRepository.findByUserName(userName)
                .map(User::fromEntity)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("userName: %s", userName)));
    }

    @Transactional
    public User join(String userName, String password) {
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new ConnectiApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName: %s", userName));
        });

        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    @Transactional
    public String login(String userName, String password) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new ConnectiApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));

        if (!encoder.matches(password, userEntity.getPassword())) {
            throw new ConnectiApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        return jwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);
    }
}
