package com.example.apigateway.mapper;

import com.example.apigateway.dto.GatewayRouteRequest;
import com.example.apigateway.dto.GatewayRouteResponse;
import com.example.apigateway.entity.GatewayRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GatewayRouteMapper Tests")
class GatewayRouteMapperTest {

    private GatewayRouteMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GatewayRouteMapper();
    }

    // -------------------------------------------------------------------------
    // toEntity
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toEntity: should map all fields from request to entity")
    void toEntity_mapsAllFields() {
        GatewayRouteRequest request = GatewayRouteRequest.builder()
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build();

        GatewayRoute entity = mapper.toEntity(request);

        assertThat(entity.getRouteId()).isEqualTo("user-service");
        assertThat(entity.getUri()).isEqualTo("http://localhost:8081");
        assertThat(entity.getPath()).isEqualTo("/api/users/**");
        assertThat(entity.getStripPrefix()).isEqualTo(1);
        assertThat(entity.getActive()).isTrue();
    }

    @Test
    @DisplayName("toEntity: should set id and timestamps to null (managed by JPA)")
    void toEntity_doesNotSetIdOrTimestamps() {
        GatewayRouteRequest request = GatewayRouteRequest.builder()
                .routeId("order-service")
                .uri("http://localhost:8082")
                .path("/api/orders/**")
                .active(true)
                .build();

        GatewayRoute entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("toEntity: should map null stripPrefix when not provided")
    void toEntity_nullStripPrefix() {
        GatewayRouteRequest request = GatewayRouteRequest.builder()
                .routeId("product-service")
                .uri("http://localhost:8083")
                .path("/api/products/**")
                .stripPrefix(null)
                .active(false)
                .build();

        GatewayRoute entity = mapper.toEntity(request);

        assertThat(entity.getStripPrefix()).isNull();
        assertThat(entity.getActive()).isFalse();
    }

    // -------------------------------------------------------------------------
    // toResponse
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toResponse: should map all fields from entity to response")
    void toResponse_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        GatewayRoute entity = GatewayRoute.builder()
                .id(10L)
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        GatewayRouteResponse response = mapper.toResponse(entity);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getRouteId()).isEqualTo("user-service");
        assertThat(response.getUri()).isEqualTo("http://localhost:8081");
        assertThat(response.getPath()).isEqualTo("/api/users/**");
        assertThat(response.getStripPrefix()).isEqualTo(1);
        assertThat(response.getActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toResponse: should map null timestamps when entity has no timestamps")
    void toResponse_nullTimestamps() {
        GatewayRoute entity = GatewayRoute.builder()
                .id(1L)
                .routeId("svc")
                .uri("http://localhost:9000")
                .path("/svc/**")
                .active(true)
                .createdAt(null)
                .updatedAt(null)
                .build();

        GatewayRouteResponse response = mapper.toResponse(entity);

        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("toResponse: should map inactive route correctly")
    void toResponse_inactiveRoute() {
        GatewayRoute entity = GatewayRoute.builder()
                .id(5L)
                .routeId("deprecated-service")
                .uri("http://localhost:9999")
                .path("/old/**")
                .active(false)
                .build();

        GatewayRouteResponse response = mapper.toResponse(entity);

        assertThat(response.getActive()).isFalse();
        assertThat(response.getId()).isEqualTo(5L);
    }

    // -------------------------------------------------------------------------
    // updateEntityFromRequest
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateEntityFromRequest: should update mutable fields on the entity")
    void updateEntityFromRequest_updatesMutableFields() {
        GatewayRoute entity = GatewayRoute.builder()
                .id(1L)
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build();

        GatewayRouteRequest updateRequest = GatewayRouteRequest.builder()
                .routeId("user-service")
                .uri("http://localhost:9091")
                .path("/api/v2/users/**")
                .stripPrefix(2)
                .active(false)
                .build();

        mapper.updateEntityFromRequest(entity, updateRequest);

        assertThat(entity.getUri()).isEqualTo("http://localhost:9091");
        assertThat(entity.getPath()).isEqualTo("/api/v2/users/**");
        assertThat(entity.getStripPrefix()).isEqualTo(2);
        assertThat(entity.getActive()).isFalse();
    }

    @Test
    @DisplayName("updateEntityFromRequest: should not change routeId or id (immutable fields)")
    void updateEntityFromRequest_doesNotChangeImmutableFields() {
        GatewayRoute entity = GatewayRoute.builder()
                .id(1L)
                .routeId("original-route-id")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .active(true)
                .build();

        GatewayRouteRequest updateRequest = GatewayRouteRequest.builder()
                .routeId("different-route-id")
                .uri("http://localhost:9091")
                .path("/api/v2/users/**")
                .active(true)
                .build();

        mapper.updateEntityFromRequest(entity, updateRequest);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getRouteId()).isEqualTo("original-route-id");
    }

    @Test
    @DisplayName("updateEntityFromRequest: should set stripPrefix to null when request has null")
    void updateEntityFromRequest_nullStripPrefix() {
        GatewayRoute entity = GatewayRoute.builder()
                .id(1L)
                .routeId("svc")
                .uri("http://localhost:8081")
                .path("/api/**")
                .stripPrefix(1)
                .active(true)
                .build();

        GatewayRouteRequest updateRequest = GatewayRouteRequest.builder()
                .routeId("svc")
                .uri("http://localhost:8081")
                .path("/api/**")
                .stripPrefix(null)
                .active(true)
                .build();

        mapper.updateEntityFromRequest(entity, updateRequest);

        assertThat(entity.getStripPrefix()).isNull();
    }
}
