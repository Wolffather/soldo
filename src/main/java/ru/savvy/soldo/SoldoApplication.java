package ru.savvy.soldo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SoldoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoldoApplication.class, args);
	}

}
