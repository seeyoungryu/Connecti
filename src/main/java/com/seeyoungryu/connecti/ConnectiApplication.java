package com.seeyoungryu.connecti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ConnectiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectiApplication.class, args);
    }

}
