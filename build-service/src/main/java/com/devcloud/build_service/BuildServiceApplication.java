package com.devcloud.build_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BuildServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildServiceApplication.class, args);
	}

}
