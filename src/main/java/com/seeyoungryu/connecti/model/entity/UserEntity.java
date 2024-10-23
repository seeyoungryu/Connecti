package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table
@Entity
public class UserEntity {

    @Column(name = "user_name")
    public String userName;
    @Column
    public String password;
    @Id
    @GeneratedValue
    private Long id;


}
