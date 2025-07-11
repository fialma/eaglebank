package com.eaglebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Enable JPA Auditing for automatic timestamping
public class EagleBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(EagleBankApplication.class, args);
	}

}
