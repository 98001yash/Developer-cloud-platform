package com.devcloud.deployment_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {


    @Bean(name = "deploymentExecutor")
    public  Executor deployExecutor() {
        return Executors.newFixedThreadPool(5);
    }
}
