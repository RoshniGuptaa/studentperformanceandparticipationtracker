package com.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages ="com.performance" )
public class StudentperformancetrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentperformancetrackerApplication.class, args);
	}

}
