package com.seeyoungryu.connecti.repository;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {   // id -> Long 타입임
    Optional<PostEntity> findById(Long postId);

}
