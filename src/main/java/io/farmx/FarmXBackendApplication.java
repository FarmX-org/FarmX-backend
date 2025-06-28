package io.farmx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FarmXBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmXBackendApplication.class, args);
	}

}
