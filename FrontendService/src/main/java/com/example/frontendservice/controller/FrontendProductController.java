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
        logger.info("Запрос списка продуктов для гостей.");

        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductBasicDTO>>() {})
                .doOnNext(products -> {
                    logger.info("Получено {} продуктов для гостя.", products.size());
                    model.addAttribute("products", products);
                    model.addAttribute("isUserAuthenticated", false); // Гость
                })
                .thenReturn("products/list-products")
                .onErrorResume(e -> {
                    logger.error("Ошибка при получении продуктов для гостя: {}", e.getMessage(), e);
                    model.addAttribute("errorMessage", "Не удалось загрузить продукты: " + e.getMessage());
                    model.addAttribute("isUserAuthenticated", false); // Гость
                    return Mono.just("products/list-products");
                });
    }

    @GetMapping("/products/list")
    public Mono<String> listProductsWithToken(
            @RequestParam("token") String token,
            Model model) {
        logger.info("Запрос продуктов для авторизованного пользователя с токеном: {}", token);

        return webClient.get()
                .uri("/products")
                .headers(headers -> headers.set("Authorization", "Bearer "+ token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductDetailedDTO>>() {})
                .doOnNext(products -> {
                    logger.info("Получено {} продуктов для авторизованного пользователя.", products.size());
                    model.addAttribute("products", products);
                    model.addAttribute("isUserAuthenticated", true); // Авторизованный пользователь
                    model.addAttribute("token", token);
                })
                .thenReturn("products/authList-products")
                .onErrorResume(e -> {
                    logger.error("Ошибка при получении продуктов для авторизованного пользователя: {}", e.getMessage(), e);
                    model.addAttribute("errorMessage", "Не удалось загрузить продукты: " + e.getMessage());
                    model.addAttribute("isUserAuthenticated", true); // Даже в случае ошибки считаем пользователя авторизованным
                    model.addAttribute("token", token);
                    return Mono.just("products/error");
                });
    }

    @GetMapping("/products/list-with-admin-check")
    public Mono<String> listProductsWithAdminCheck(
            @RequestParam("token") String token,
            Model model) {
        logger.info("Проверка роли администратора для токена: {}", token);

        return isAdmin(token)
                .flatMap(isAdmin -> {
                    logger.info("Администратор: {}", isAdmin);
                    return webClient.get()
                            .uri("/products")
                            .headers(headers -> headers.set("Authorization", "Bearer "+ token))
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<List<ProductDetailedDTO>>() {})
                            .doOnNext(products -> {
                                logger.info("Получено {} продуктов для пользователя с токеном: {}", products.size(), token);
                                model.addAttribute("products", products);
                                model.addAttribute("isUserAuthenticated", true); // Авторизованный пользователь
                                model.addAttribute("token", token);
                                model.addAttribute("isAdmin", isAdmin); // Информация о том, является ли пользователь администратором
                            })
                            .thenReturn("products/authList-products");
                })
                .onErrorResume(e -> {
                    logger.error("Ошибка при получении продуктов с проверкой роли администратора для токена {}: {}", token, e.getMessage(), e);
                    model.addAttribute("errorMessage", "Не удалось загрузить продукты: " + e.getMessage());
                    model.addAttribute("isUserAuthenticated", true); // Даже в случае ошибки считаем пользователя авторизованным
                    model.addAttribute("token", token);
                    return Mono.just("products/error");
                });
    }

    private Mono<Boolean> isAdmin(String token) {
        logger.info("Проверка, является ли пользователь с токеном {} администратором.", token);

        return webClient.get()
                .uri("/auth/user/role") // Предположим, что есть эндпоинт для получения роли пользователя
                .headers(headers -> headers.set("Authorization", "Bearer " + token))
                .retrieve()
                .bodyToMono(Boolean.class) // Ожидаем, что сервис вернет true/false, если пользователь администратор
                .doOnTerminate(() -> logger.info("Завершена проверка роли для токена: {}", token))
                .onErrorResume(e -> {
                    logger.error("Ошибка при проверке роли администратора для токена {}: {}", token, e.getMessage(), e);
                    return Mono.just(false); // В случае ошибки считаем, что это не администратор
                });
    }
}
