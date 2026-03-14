package com.example.apigateway.controller;

import com.example.apigateway.dto.GatewayRouteRequest;
import com.example.apigateway.dto.GatewayRouteResponse;
import com.example.apigateway.service.GatewayRouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("GatewayRouteController Integration Tests")
class GatewayRouteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GatewayRouteService routeService;

    private GatewayRouteRequest validRequest;
    private GatewayRouteResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = GatewayRouteRequest.builder()
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build();

        sampleResponse = GatewayRouteResponse.builder()
                .id(1L)
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build();
    }

    // -------------------------------------------------------------------------
    // POST /v1/apigateway/routes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /routes: should return 201 and created route")
    void createRoute_returns201() {
        when(routeService.createRoute(any(GatewayRouteRequest.class)))
                .thenReturn(Mono.just(sampleResponse));

        webTestClient.post()
                .uri("/v1/apigateway/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GatewayRouteResponse.class)
                .isEqualTo(sampleResponse);
    }

    @Test
    @DisplayName("POST /routes: should return 409 when routeId already exists")
    void createRoute_duplicateRouteId_returns409() {
        when(routeService.createRoute(any(GatewayRouteRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Route already exists")));

        webTestClient.post()
                .uri("/v1/apigateway/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    // -------------------------------------------------------------------------
    // GET /v1/apigateway/routes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /routes: should return 200 and list of all routes")
    void getAllRoutes_returns200() {
        GatewayRouteResponse response2 = GatewayRouteResponse.builder()
                .id(2L).routeId("order-service").active(true).build();

        when(routeService.getAllRoutes()).thenReturn(Flux.just(sampleResponse, response2));

        webTestClient.get()
                .uri("/v1/apigateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GatewayRouteResponse.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("GET /routes: should return empty list when no routes exist")
    void getAllRoutes_empty_returns200() {
        when(routeService.getAllRoutes()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/v1/apigateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GatewayRouteResponse.class)
                .hasSize(0);
    }

    // -------------------------------------------------------------------------
    // GET /v1/apigateway/routes/active
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /routes/active: should return only active routes")
    void getActiveRoutes_returns200() {
        when(routeService.getActiveRoutes()).thenReturn(Flux.just(sampleResponse));

        webTestClient.get()
                .uri("/v1/apigateway/routes/active")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GatewayRouteResponse.class)
                .hasSize(1);
    }

    // -------------------------------------------------------------------------
    // GET /v1/apigateway/routes/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /routes/{id}: should return 200 when route found")
    void getRouteById_found_returns200() {
        when(routeService.getRouteById(1L)).thenReturn(Mono.just(sampleResponse));

        webTestClient.get()
                .uri("/v1/apigateway/routes/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GatewayRouteResponse.class)
                .isEqualTo(sampleResponse);
    }

    @Test
    @DisplayName("GET /routes/{id}: should return 404 when route not found")
    void getRouteById_notFound_returns404() {
        when(routeService.getRouteById(99L))
                .thenReturn(Mono.error(new NoSuchElementException("Route not found")));

        webTestClient.get()
                .uri("/v1/apigateway/routes/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    // -------------------------------------------------------------------------
    // PUT /v1/apigateway/routes/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /routes/{id}: should return 200 with updated route")
    void updateRoute_returns200() {
        GatewayRouteResponse updated = GatewayRouteResponse.builder()
                .id(1L).routeId("user-service").uri("http://localhost:9091").active(true).build();

        when(routeService.updateRoute(eq(1L), any(GatewayRouteRequest.class)))
                .thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri("/v1/apigateway/routes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GatewayRouteResponse.class)
                .isEqualTo(updated);
    }

    @Test
    @DisplayName("PUT /routes/{id}: should return 404 when route not found")
    void updateRoute_notFound_returns404() {
        when(routeService.updateRoute(eq(99L), any(GatewayRouteRequest.class)))
                .thenReturn(Mono.error(new NoSuchElementException("Not found")));

        webTestClient.put()
                .uri("/v1/apigateway/routes/99")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    // -------------------------------------------------------------------------
    // DELETE /v1/apigateway/routes/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /routes/{id}: should return 204 on successful delete")
    void deleteRoute_returns204() {
        when(routeService.deleteRoute(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/v1/apigateway/routes/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /routes/{id}: should return 404 when route not found")
    void deleteRoute_notFound_returns404() {
        when(routeService.deleteRoute(99L))
                .thenReturn(Mono.error(new NoSuchElementException("Not found")));

        webTestClient.delete()
                .uri("/v1/apigateway/routes/99")
                .exchange()
                .expectStatus().isNotFound();
    }
}
