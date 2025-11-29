package com.example.LuckyWheel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LuckyWheelApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuckyWheelApplication.class, args);
	}

}
