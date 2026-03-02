package com.forvmom.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.forvmom"
})
@EntityScan(basePackages = {
        "com.forvmom.data.entities",
        "com.forvmom.security.entities" // If security has entities
})

public class MomentForeverApp {

    public static void main(String[] args) {
        SpringApplication.run(MomentForeverApp.class, args);
    }
}