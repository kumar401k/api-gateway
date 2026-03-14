package com.example.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayRouteResponse {

    private Long id;
    private String routeId;
    private String uri;
    private String path;
    private Integer stripPrefix;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
