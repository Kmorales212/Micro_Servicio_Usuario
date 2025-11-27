package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; 
import org.springframework.context.annotation.ComponentScan; 

@SpringBootApplication
@ComponentScan(basePackages = "com.example.demo") 
@EnableJpaRepositories(basePackages = "com.example.demo.repository") 
public class MicroServicioUsuarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServicioUsuarioApplication.class, args);
    }
}