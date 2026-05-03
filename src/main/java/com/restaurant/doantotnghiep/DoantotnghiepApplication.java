package com.restaurant.doantotnghiep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DoantotnghiepApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoantotnghiepApplication.class, args);
	}
}
