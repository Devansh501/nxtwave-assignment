package com.nxtwave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NxtwaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(NxtwaveApplication.class, args);
    }

}
