package com.example.apigateway.controller;

import com.example.apigateway.dto.GatewayRouteRequest;
import com.example.apigateway.dto.GatewayRouteResponse;
import com.example.apigateway.service.GatewayRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/apigateway/routes")
@RequiredArgsConstructor
public class GatewayRouteController {

    private final GatewayRouteService routeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GatewayRouteResponse> createRoute(@Valid @RequestBody GatewayRouteRequest request) {
        return routeService.createRoute(request);
    }

    @GetMapping
    public Flux<GatewayRouteResponse> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/active")
    public Flux<GatewayRouteResponse> getActiveRoutes() {
        return routeService.getActiveRoutes();
    }

    @GetMapping("/{id}")
    public Mono<GatewayRouteResponse> getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    @GetMapping("/routeId/{routeId}")
    public Mono<GatewayRouteResponse> getRouteByRouteId(@PathVariable String routeId) {
        return routeService.getRouteByRouteId(routeId);
    }

    @PutMapping("/{id}")
    public Mono<GatewayRouteResponse> updateRoute(@PathVariable Long id,
                                                   @Valid @RequestBody GatewayRouteRequest request) {
        return routeService.updateRoute(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteRoute(@PathVariable Long id) {
        return routeService.deleteRoute(id);
    }
}
