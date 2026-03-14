package com.example.apigateway.mapper;

import com.example.apigateway.dto.GatewayRouteRequest;
import com.example.apigateway.dto.GatewayRouteResponse;
import com.example.apigateway.entity.GatewayRoute;
import org.springframework.stereotype.Component;

@Component
public class GatewayRouteMapper {

    public GatewayRoute toEntity(GatewayRouteRequest request) {
        return GatewayRoute.builder()
                .routeId(request.getRouteId())
                .uri(request.getUri())
                .path(request.getPath())
                .stripPrefix(request.getStripPrefix())
                .active(request.getActive())
                .build();
    }

    public GatewayRouteResponse toResponse(GatewayRoute entity) {
        return GatewayRouteResponse.builder()
                .id(entity.getId())
                .routeId(entity.getRouteId())
                .uri(entity.getUri())
                .path(entity.getPath())
                .stripPrefix(entity.getStripPrefix())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequest(GatewayRoute entity, GatewayRouteRequest request) {
        entity.setUri(request.getUri());
        entity.setPath(request.getPath());
        entity.setStripPrefix(request.getStripPrefix());
        entity.setActive(request.getActive());
    }
}
