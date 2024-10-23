package com.seeyoungryu.connecti.repository;

import com.seeyoungryu.connecti.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> { // <T,ID>
}
