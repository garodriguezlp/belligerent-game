package com.example.belligerentgame;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    @Order(1)
    public CommandLineRunner postmanRunner(WebClient client) {
        return args -> callMonitored("postman", () -> client.get()
                .uri("https://postman-echo.com/get")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block());
    }

    @Bean
    @Order(2)
    public CommandLineRunner twitterRunner(WebClient client, @Value("${twitter.api-key}") String apiKey) {
        return args -> callMonitored("twitter", () -> client.get()
                .uri("https://api.twitter.com/2/tweets/search/stream/rules")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(apiKey))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block());
    }

    static void callMonitored(String apiName, Callable<JsonNode> jsonNodeCallable) throws Exception {
        long startTime = System.currentTimeMillis();

        System.out.println("--- ------------------------------------------------------------------------------------------------");
        System.out.println("[GUSTAVO DEBUG] Calling " + apiName + " api...");
        System.out.println("--- ------------------------------------------------------------------------------------------------");

        JsonNode response = jsonNodeCallable.call();
        System.out.println("--- ------------------------------------------------------------------------------------------------");
        System.out.println("[GUSTAVO DEBUG] Response: " + response.toPrettyString());
        System.out.println("--- ------------------------------------------------------------------------------------------------");

        System.out.println("--- ------------------------------------------------------------------------------------------------");
        System.out.println("[GUSTAVO DEBUG] Time taken: " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
        System.out.println("--- ------------------------------------------------------------------------------------------------");
    }
}
