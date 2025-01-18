package org.example.expert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@SpringBootApplication
@EnableSpringDataWebSupport
public class ExpertApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpertApplication.class, args);
    }
}
