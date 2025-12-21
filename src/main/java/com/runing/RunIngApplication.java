package com.runing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class RunIngApplication {

	public static void main(String[] args) {
		SpringApplication.run(RunIngApplication.class, args);
	}

}
