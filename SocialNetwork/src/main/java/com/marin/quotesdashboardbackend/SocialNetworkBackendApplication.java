package com.marin.quotesdashboardbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialNetworkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkBackendApplication.class, args);
    }

}
