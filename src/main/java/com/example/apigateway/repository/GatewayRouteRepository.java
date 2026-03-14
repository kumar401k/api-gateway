package com.example.apigateway.repository;

import com.example.apigateway.entity.GatewayRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatewayRouteRepository extends JpaRepository<GatewayRoute, Long> {

    Optional<GatewayRoute> findByRouteId(String routeId);

    List<GatewayRoute> findByActiveTrue();

    boolean existsByRouteId(String routeId);
}
