package com.healthily.symptomchecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.healthily" })
public class SymptomCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SymptomCheckerApplication.class, args);
	}

}
