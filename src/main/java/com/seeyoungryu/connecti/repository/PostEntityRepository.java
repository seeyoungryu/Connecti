package com.seeyoungryu.connecti.repository;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import com.seeyoungryu.connecti.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {   // id -> Long 타입
    Optional<PostEntity> findById(Long postId);

    Page<PostEntity> findAllByUserId(UserEntity userEntity, Pageable pageable);
}
