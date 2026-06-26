package com.amazon.seller.devopsportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DevOpsDeveloperPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevOpsDeveloperPortalApplication.class, args);
    }
}
