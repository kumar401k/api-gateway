package com.example.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/apigateway/demo")
public class GatewayDemoController {

    @GetMapping("/test")
    public String getUsers() {
        return "test api gateway";
    }
}
