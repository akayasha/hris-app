package com.hris;

import com.hris.config.PostgresDatabaseBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrisApplication {
    public static void main(String[] args) {
        PostgresDatabaseBootstrap.ensureDatabaseExists();
        SpringApplication.run(HrisApplication.class, args);
    }
}
