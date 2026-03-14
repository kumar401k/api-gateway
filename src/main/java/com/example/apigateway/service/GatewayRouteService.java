package com.example.apigateway.service;

import com.example.apigateway.dto.GatewayRouteRequest;
import com.example.apigateway.dto.GatewayRouteResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GatewayRouteService {

    Mono<GatewayRouteResponse> createRoute(GatewayRouteRequest request);

    Mono<GatewayRouteResponse> getRouteById(Long id);

    Mono<GatewayRouteResponse> getRouteByRouteId(String routeId);

    Flux<GatewayRouteResponse> getAllRoutes();

    Flux<GatewayRouteResponse> getActiveRoutes();

    Mono<GatewayRouteResponse> updateRoute(Long id, GatewayRouteRequest request);

    Mono<Void> deleteRoute(Long id);
}
