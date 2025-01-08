package com.example.frontendservice.controller;

import com.example.frontendservice.client.productDTO.ProductBasicDTO;
import com.example.frontendservice.client.productDTO.ProductDetailedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Controller
public class FrontendProductController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(FrontendProductController.class);

    public FrontendProductController(@Value("${gateway.url}") String gatewayUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(gatewayUrl)
                .build();
    }

    @GetMapping("/products")
    public Mono<String> listProducts(Model model) {
        return webClient.get()
                .uri("/products") // Путь для получения списка продуктов
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductBasicDTO>>() {}) // Получаем список продуктов
                .doOnNext(products -> model.addAttribute("products", products)) // Добавляем список в модель
                .thenReturn("products/list-products")
                .onErrorResume(e -> {
                    logger.error("Error fetching products: {}", e.getMessage(), e);
                    model.addAttribute("errorMessage", "Failed to load products: " + e.getMessage());
                    return Mono.just("products/list-products");
                });
    }


}
