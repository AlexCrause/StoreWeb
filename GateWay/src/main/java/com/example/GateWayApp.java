package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GateWayApp {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApp.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("AuthService", r -> r.path("/serviceAuth/**")
                        .uri("lb://AuthService"))  // Изменено на lb:// для Eureka
                .route("ProductService", r -> r.path("/serviceProduct/**")
                        .uri("lb://ProductService"))  // Аналогично для ProductService
                .route("OrderService", r -> r.path("/serviceOrder/**")
                        .uri("lb://OrderService"))  // И для OrderService
                .build();
    }
}
