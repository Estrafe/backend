package me.diegxherrera.estrafebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "me.diegxherrera.estrafebackend.model")
public class EstrafeBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(EstrafeBackendApplication.class, args);
	}
}
