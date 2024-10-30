package com.seeyoungryu.connecti.repository;

import com.seeyoungryu.connecti.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //@Repository: Spring이 이 인터페이스를 리포지토리로 인식하고 관리하게 함

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> { // <T,ID>
    Optional<UserEntity> findByUserName(String userName);   //return값이 없을수도 있으니 Optional<T>
}


