package com.example.apigateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayRouteRequest {

    @NotBlank(message = "routeId must not be blank")
    private String routeId;

    @NotBlank(message = "uri must not be blank")
    private String uri;

    @NotBlank(message = "path must not be blank")
    private String path;

    private Integer stripPrefix;

    @NotNull(message = "active must not be null")
    private Boolean active;
}
