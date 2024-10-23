package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table
@Entity
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

}
