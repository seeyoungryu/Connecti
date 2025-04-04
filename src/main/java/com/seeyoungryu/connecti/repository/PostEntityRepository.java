package com.seeyoungryu.connecti.repository;

import com.seeyoungryu.connecti.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findAllByUserId(Long userId, Pageable pageable);
}
