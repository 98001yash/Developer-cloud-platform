package com.devcloud.deployment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DeploymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeploymentServiceApplication.class, args);
	}

}
