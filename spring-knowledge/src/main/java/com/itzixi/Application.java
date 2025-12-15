package com.itzixi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.itzixi.mapper")
@ComponentScan(basePackages = "com.itzixi")
public class Application {

    //    http://localhost:8080/hello/world
    //    http://127.0.0.1:8080/hello/world

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}