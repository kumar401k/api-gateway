package com.example.apigateway.config;

import com.example.apigateway.entity.GatewayRoute;
import com.example.apigateway.repository.GatewayRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the H2 in-memory database with sample gateway routes on startup.
 * This runs only when the table is empty, making it safe for restarts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GatewayRouteRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            log.info("Gateway routes already seeded, skipping initialization.");
            return;
        }

        repository.save(GatewayRoute.builder()
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build());

        repository.save(GatewayRoute.builder()
                .routeId("order-service")
                .uri("http://localhost:8082")
                .path("/api/orders/**")
                .stripPrefix(1)
                .active(true)
                .build());

        repository.save(GatewayRoute.builder()
                .routeId("product-service")
                .uri("http://localhost:8083")
                .path("/api/products/**")
                .stripPrefix(1)
                .active(false)
                .build());

        log.info("Seeded {} gateway routes into H2.", repository.count());
    }
}
