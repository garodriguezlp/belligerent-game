package com.example.belligerentgame;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.Callable;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.channel.ChannelOption.TCP_NODELAY;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .option(CONNECT_TIMEOUT_MILLIS, 5000)
                .option(TCP_NODELAY, true)
                .responseTimeout(Duration.ofMillis(5000));
    }

    @Bean
    public WebClient webClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
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
