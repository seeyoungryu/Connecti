package com.seeyoungryu.connecti.repository;

import com.seeyoungryu.connecti.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> { // <T,ID>
    Optional<UserEntity> findByUserName(String userName);   //return값이 없을수도~ Optional<T>
}


