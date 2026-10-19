package com.thebrian31.coworking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CoworkingApplication {

    public static void main(String[] args) {

        SpringApplication.run(CoworkingApplication.class, args);
    }

}
