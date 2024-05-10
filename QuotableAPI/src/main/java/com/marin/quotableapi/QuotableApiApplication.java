package com.marin.quotableapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class QuotableApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuotableApiApplication.class, args);
    }

}
