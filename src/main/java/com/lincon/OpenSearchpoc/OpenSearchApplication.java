package com.lincon.OpenSearchpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.lincon.OpenSearchpoc")
public class OpenSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenSearchApplication.class, args);
	}

}
