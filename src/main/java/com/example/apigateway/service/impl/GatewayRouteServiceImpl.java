package com.example.apigateway.service.impl;

import com.example.apigateway.dto.GatewayRouteRequest;
import com.example.apigateway.dto.GatewayRouteResponse;
import com.example.apigateway.entity.GatewayRoute;
import com.example.apigateway.mapper.GatewayRouteMapper;
import com.example.apigateway.repository.GatewayRouteRepository;
import com.example.apigateway.service.GatewayRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.NoSuchElementException;

/**
 * Service implementation that bridges blocking JPA calls into the reactive pipeline.
 *
 * Spring Cloud Gateway runs on a Netty event loop which must never be blocked.
 * Every JPA/JDBC operation is offloaded to Schedulers.boundedElastic() — a thread
 * pool designed for blocking I/O — using Mono.fromCallable() which defers execution
 * until subscription time on the designated scheduler.
 */
@Service
@RequiredArgsConstructor
public class GatewayRouteServiceImpl implements GatewayRouteService {

    private final GatewayRouteRepository repository;
    private final GatewayRouteMapper mapper;

    @Override
    @Transactional
    public Mono<GatewayRouteResponse> createRoute(GatewayRouteRequest request) {
        return Mono.fromCallable(() -> {
                    if (repository.existsByRouteId(request.getRouteId())) {
                        throw new IllegalArgumentException(
                                "Route with routeId '" + request.getRouteId() + "' already exists");
                    }
                    GatewayRoute saved = repository.save(mapper.toEntity(request));
                    return mapper.toResponse(saved);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<GatewayRouteResponse> getRouteById(Long id) {
        return Mono.fromCallable(() -> repository.findById(id)
                        .map(mapper::toResponse)
                        .orElseThrow(() -> new NoSuchElementException("Route not found with id: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<GatewayRouteResponse> getRouteByRouteId(String routeId) {
        return Mono.fromCallable(() -> repository.findByRouteId(routeId)
                        .map(mapper::toResponse)
                        .orElseThrow(() -> new NoSuchElementException("Route not found with routeId: " + routeId)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GatewayRouteResponse> getAllRoutes() {
        return Mono.fromCallable(() -> repository.findAll()
                        .stream()
                        .map(mapper::toResponse)
                        .toList())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GatewayRouteResponse> getActiveRoutes() {
        return Mono.fromCallable(() -> repository.findByActiveTrue()
                        .stream()
                        .map(mapper::toResponse)
                        .toList())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<GatewayRouteResponse> updateRoute(Long id, GatewayRouteRequest request) {
        return Mono.fromCallable(() -> {
                    GatewayRoute existing = repository.findById(id)
                            .orElseThrow(() -> new NoSuchElementException("Route not found with id: " + id));
                    mapper.updateEntityFromRequest(existing, request);
                    return mapper.toResponse(repository.save(existing));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> deleteRoute(Long id) {
        return Mono.fromRunnable(() -> {
                    if (!repository.existsById(id)) {
                        throw new NoSuchElementException("Route not found with id: " + id);
                    }
                    repository.deleteById(id);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
