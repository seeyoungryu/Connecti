package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;

@Table
@Entity
public class UserEntity {


    @Column(name = "user_name")
    public String userName;
    @Id
    @GeneratedValue
    private Long id;


}
