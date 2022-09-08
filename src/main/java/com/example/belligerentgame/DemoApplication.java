package com.example.belligerentgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		System.out.println("Performing the call");
		WebClient client = WebClient.builder().build();
		client.get()
				.uri("https://postman-echo.com/get")
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}

}
