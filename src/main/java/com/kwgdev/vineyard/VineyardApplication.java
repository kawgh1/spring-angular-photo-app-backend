package com.kwgdev.vineyard;

import com.kwgdev.vineyard.utility.EmailConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.thymeleaf.TemplateEngine;

@SpringBootApplication
public class VineyardApplication {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(VineyardApplication.class, args);
	}

}
