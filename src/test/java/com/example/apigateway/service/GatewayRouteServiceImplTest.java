package com.example.apigateway.service;

import com.example.apigateway.dto.GatewayRouteRequest;
import com.example.apigateway.dto.GatewayRouteResponse;
import com.example.apigateway.entity.GatewayRoute;
import com.example.apigateway.mapper.GatewayRouteMapper;
import com.example.apigateway.repository.GatewayRouteRepository;
import com.example.apigateway.service.impl.GatewayRouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatewayRouteServiceImpl Tests")
class GatewayRouteServiceImplTest {

    @Mock
    private GatewayRouteRepository repository;

    @Mock
    private GatewayRouteMapper mapper;

    @InjectMocks
    private GatewayRouteServiceImpl service;

    private GatewayRouteRequest request;
    private GatewayRoute entity;
    private GatewayRouteResponse response;

    @BeforeEach
    void setUp() {
        request = GatewayRouteRequest.builder()
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build();

        entity = GatewayRoute.builder()
                .id(1L)
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        response = GatewayRouteResponse.builder()
                .id(1L)
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build();
    }

    // -------------------------------------------------------------------------
    // createRoute
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createRoute: should create and return a route when routeId is unique")
    void createRoute_success() {
        when(repository.existsByRouteId("user-service")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        StepVerifier.create(service.createRoute(request))
                .expectNextMatches(r -> r.getRouteId().equals("user-service") && r.getId().equals(1L))
                .verifyComplete();

        verify(repository).existsByRouteId("user-service");
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("createRoute: should error with IllegalArgumentException when routeId already exists")
    void createRoute_duplicateRouteId_throwsException() {
        when(repository.existsByRouteId("user-service")).thenReturn(true);

        StepVerifier.create(service.createRoute(request))
                .expectErrorMatches(ex ->
                        ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("user-service"))
                .verify();

        verify(repository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // getRouteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getRouteById: should return route when found")
    void getRouteById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        StepVerifier.create(service.getRouteById(1L))
                .expectNextMatches(r -> r.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("getRouteById: should error with NoSuchElementException when not found")
    void getRouteById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(service.getRouteById(99L))
                .expectErrorMatches(ex ->
                        ex instanceof NoSuchElementException &&
                        ex.getMessage().contains("99"))
                .verify();
    }

    // -------------------------------------------------------------------------
    // getRouteByRouteId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getRouteByRouteId: should return route when found")
    void getRouteByRouteId_found() {
        when(repository.findByRouteId("user-service")).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        StepVerifier.create(service.getRouteByRouteId("user-service"))
                .expectNextMatches(r -> r.getRouteId().equals("user-service"))
                .verifyComplete();
    }

    @Test
    @DisplayName("getRouteByRouteId: should error when not found")
    void getRouteByRouteId_notFound() {
        when(repository.findByRouteId(anyString())).thenReturn(Optional.empty());

        StepVerifier.create(service.getRouteByRouteId("ghost"))
                .expectErrorMatches(ex -> ex instanceof NoSuchElementException)
                .verify();
    }

    // -------------------------------------------------------------------------
    // getAllRoutes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllRoutes: should emit all routes")
    void getAllRoutes_returnsAll() {
        GatewayRoute entity2 = GatewayRoute.builder().id(2L).routeId("order-service").build();
        GatewayRouteResponse response2 = GatewayRouteResponse.builder().id(2L).routeId("order-service").build();

        when(repository.findAll()).thenReturn(List.of(entity, entity2));
        when(mapper.toResponse(entity)).thenReturn(response);
        when(mapper.toResponse(entity2)).thenReturn(response2);

        StepVerifier.create(service.getAllRoutes())
                .expectNext(response)
                .expectNext(response2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getAllRoutes: should complete without items when table is empty")
    void getAllRoutes_empty() {
        when(repository.findAll()).thenReturn(List.of());

        StepVerifier.create(service.getAllRoutes())
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // getActiveRoutes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getActiveRoutes: should emit only active routes")
    void getActiveRoutes_returnsOnlyActive() {
        when(repository.findByActiveTrue()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        StepVerifier.create(service.getActiveRoutes())
                .expectNextMatches(GatewayRouteResponse::getActive)
                .verifyComplete();
    }

    // -------------------------------------------------------------------------
    // updateRoute
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateRoute: should update fields and return updated response")
    void updateRoute_success() {
        GatewayRouteRequest updateRequest = GatewayRouteRequest.builder()
                .routeId("user-service")
                .uri("http://localhost:9091")
                .path("/api/v2/users/**")
                .stripPrefix(2)
                .active(true)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(mapper).updateEntityFromRequest(entity, updateRequest);
        when(repository.save(entity)).thenReturn(entity);

        GatewayRouteResponse updated = GatewayRouteResponse.builder()
                .id(1L).routeId("user-service").uri("http://localhost:9091").build();
        when(mapper.toResponse(entity)).thenReturn(updated);

        StepVerifier.create(service.updateRoute(1L, updateRequest))
                .expectNextMatches(r -> r.getUri().equals("http://localhost:9091"))
                .verifyComplete();

        verify(mapper).updateEntityFromRequest(entity, updateRequest);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("updateRoute: should error when route not found")
    void updateRoute_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(service.updateRoute(99L, request))
                .expectErrorMatches(ex ->
                        ex instanceof NoSuchElementException &&
                        ex.getMessage().contains("99"))
                .verify();

        verify(repository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteRoute
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteRoute: should delete route when it exists")
    void deleteRoute_success() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        StepVerifier.create(service.deleteRoute(1L))
                .verifyComplete();

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteRoute: should error with NoSuchElementException when route not found")
    void deleteRoute_notFound() {
        when(repository.existsById(99L)).thenReturn(false);

        StepVerifier.create(service.deleteRoute(99L))
                .expectErrorMatches(ex ->
                        ex instanceof NoSuchElementException &&
                        ex.getMessage().contains("99"))
                .verify();

        verify(repository, never()).deleteById(any());
    }
}
