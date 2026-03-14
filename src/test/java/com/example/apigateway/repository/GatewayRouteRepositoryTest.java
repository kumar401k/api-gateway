package com.example.apigateway.repository;

import com.example.apigateway.entity.GatewayRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("GatewayRouteRepository Tests")
class GatewayRouteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GatewayRouteRepository repository;

    private GatewayRoute sampleRoute;

    @BeforeEach
    void setUp() {
        sampleRoute = GatewayRoute.builder()
                .routeId("user-service")
                .uri("http://localhost:8081")
                .path("/api/users/**")
                .stripPrefix(1)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should save a route and assign an auto-generated ID")
    void shouldSaveRoute() {
        GatewayRoute saved = repository.save(sampleRoute);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find a route by its business routeId")
    void shouldFindByRouteId() {
        entityManager.persistAndFlush(sampleRoute);

        Optional<GatewayRoute> found = repository.findByRouteId("user-service");

        assertThat(found).isPresent();
        assertThat(found.get().getUri()).isEqualTo("http://localhost:8081");
        assertThat(found.get().getPath()).isEqualTo("/api/users/**");
    }

    @Test
    @DisplayName("Should return empty when routeId does not exist")
    void shouldReturnEmptyWhenRouteIdNotFound() {
        Optional<GatewayRoute> found = repository.findByRouteId("non-existent");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return only active routes")
    void shouldFindOnlyActiveRoutes() {
        GatewayRoute activeRoute = GatewayRoute.builder()
                .routeId("active-service")
                .uri("http://localhost:8081")
                .path("/active/**")
                .active(true)
                .build();

        GatewayRoute inactiveRoute = GatewayRoute.builder()
                .routeId("inactive-service")
                .uri("http://localhost:8082")
                .path("/inactive/**")
                .active(false)
                .build();

        entityManager.persist(activeRoute);
        entityManager.persist(inactiveRoute);
        entityManager.flush();

        List<GatewayRoute> activeRoutes = repository.findByActiveTrue();

        assertThat(activeRoutes).hasSize(1);
        assertThat(activeRoutes.get(0).getRouteId()).isEqualTo("active-service");
    }

    @Test
    @DisplayName("Should return true when routeId already exists")
    void shouldReturnTrueForExistingRouteId() {
        entityManager.persistAndFlush(sampleRoute);

        boolean exists = repository.existsByRouteId("user-service");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when routeId does not exist")
    void shouldReturnFalseForNonExistingRouteId() {
        boolean exists = repository.existsByRouteId("ghost-service");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should delete a route by ID")
    void shouldDeleteRouteById() {
        GatewayRoute saved = entityManager.persistAndFlush(sampleRoute);
        Long id = saved.getId();

        repository.deleteById(id);
        entityManager.flush();

        Optional<GatewayRoute> found = repository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should retrieve all persisted routes")
    void shouldFindAllRoutes() {
        GatewayRoute route2 = GatewayRoute.builder()
                .routeId("order-service")
                .uri("http://localhost:8082")
                .path("/api/orders/**")
                .active(true)
                .build();

        entityManager.persist(sampleRoute);
        entityManager.persist(route2);
        entityManager.flush();

        List<GatewayRoute> all = repository.findAll();

        assertThat(all).hasSize(2);
    }
}
