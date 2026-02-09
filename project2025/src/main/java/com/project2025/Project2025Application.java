package com.project2025;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Project2025Application {

	public static void main(String[] args) {
		SpringApplication.run(Project2025Application.class, args);
	}

}
