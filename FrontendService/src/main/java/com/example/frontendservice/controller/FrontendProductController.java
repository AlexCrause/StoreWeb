package com.example.frontendservice.controller;

import com.example.frontendservice.client.productDTO.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    // Список продуктов
    @GetMapping("/products")
    public Mono<String> listProducts(Model model) {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("products/list-products")
                .onErrorResume(e -> {
                    logger.error("Error fetching products: {}", e.getMessage(), e);
                    model.addAttribute("errorMessage", "Failed to load products: " + e.getMessage());
                    return Mono.just("products/list-products"); // Показываем страницу с ошибкой
                });
    }

    // Форма для добавления продукта
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/add-product"; // Убедитесь, что шаблон существует
    }

    // Добавление продукта
    @PostMapping("/products/add")
    public Mono<String> addProduct(@ModelAttribute Product product, Model model) {
        return webClient.post()
                .uri("/products")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .thenReturn("redirect:/products")
                .onErrorResume(e -> {
                    logger.error("Error adding product: {}", e.getMessage(), e);
                    model.addAttribute("errorMessage", "Failed to add product: " + e.getMessage());
                    return Mono.just("products/add-product");
                });
    }

    // Удаление продукта
    @DeleteMapping("/products/{id}")
    public Mono<String> deleteProduct(@PathVariable UUID id, Model model) {
        return webClient.delete()
                .uri("/products/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .thenReturn("redirect:/products")
                .onErrorResume(e -> {
                    logger.error("Error deleting product: {}", e.getMessage(), e);
                    model.addAttribute("errorMessage", "Failed to delete product: " + e.getMessage());
                    return Mono.just("products/list-products");
                });
    }
}
