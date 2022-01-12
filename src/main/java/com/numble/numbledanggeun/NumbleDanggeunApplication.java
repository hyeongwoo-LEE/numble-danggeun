package com.numble.numbledanggeun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NumbleDanggeunApplication {

	public static void main(String[] args) {
		SpringApplication.run(NumbleDanggeunApplication.class, args);
	}

}
