package com.example.agrilinkback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.agrilinkback.module")
@SpringBootApplication
public class AgriLinkBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriLinkBackApplication.class, args);
    }

}
