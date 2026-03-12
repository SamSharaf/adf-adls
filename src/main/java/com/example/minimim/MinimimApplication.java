package com.example.minimim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MinimimApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinimimApplication.class, args);
    }
}
