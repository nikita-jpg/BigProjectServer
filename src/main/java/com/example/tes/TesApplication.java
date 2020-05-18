package com.example.tes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TesApplication {

    public static void main(String[] args) {
        LocalBase localBase = new LocalBase();
        SpringApplication.run(TesApplication.class, args);
    }

}
