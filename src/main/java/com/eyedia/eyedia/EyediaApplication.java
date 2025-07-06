package com.eyedia.eyedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EyediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EyediaApplication.class, args);
    }

}
