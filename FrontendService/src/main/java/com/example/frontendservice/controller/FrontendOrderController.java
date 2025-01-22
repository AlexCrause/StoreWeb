package com.example.frontendservice.controller;

import com.example.frontendservice.client.orderDTO.CartDTO;
import com.example.frontendservice.client.orderDTO.CartItemDTO;
import com.example.frontendservice.client.productDTO.ProductDetailedDTO;
import com.example.frontendservice.client.reviewDTO.ReviewCreateDTO;
import com.example.frontendservice.client.reviewDTO.ReviewResponseDTO;
import com.example.frontendservice.client.userDTO.UserDTO;
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
@RequestMapping("/frontend")
public class FrontendOrderController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(FrontendOrderController.class);

    /**
     * Конструктор для инициализации WebClient с использованием URL шлюза.
     *
     * @param gatewayUrl Базовый URL шлюза.
     */
    public FrontendOrderController(@Value("${gateway.url}") String gatewayUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(gatewayUrl)
                .build();
    }


    @GetMapping("/viewCart")
    public Mono<String> viewCart(@RequestParam UUID productId,
                                 @RequestParam String token,
                                 Model model) {
        return webClient.get()
                .uri("/products/" + productId) // Получение данных о продукте
                .headers(headers -> headers.set("Authorization", "Bearer " + token))
                .retrieve()
                .bodyToMono(ProductDetailedDTO.class)
                .flatMap(product -> {
                    model.addAttribute("product", product); // Добавление данных о продукте в модель
                    model.addAttribute("productId", productId); // Добавление productId в модель
                    model.addAttribute("token", token); // Добавление токена в модель
                    return webClient.get()
                            .uri("/orders/product/{productId}", productId)
                            .headers(headers -> headers.set("Authorization", "Bearer " + token))
                            .retrieve()
                            .bodyToMono(CartDTO.class);
                })
                .flatMap(cart -> {
                    model.addAttribute("cart", cart);
                    return Mono.just("products/cart");
                })

                .onErrorResume(ex -> {
                    logger.error("Ошибка при загрузке данных: {}", ex.getMessage(), ex);
                    model.addAttribute("errorMessage", "Не удалось загрузить данные: " + ex.getMessage());
                    return Mono.just("products/error"); // Возврат представления с ошибкой в случае неудачи
                });
    }


    @PostMapping("/addToCart")
    public Mono<String> createOrder(
            @ModelAttribute CartItemDTO cartItemDTO,
            @RequestParam("productId") UUID productId,
            @RequestParam("token") String token) {

        cartItemDTO.setProductId(productId);

        return webClient
                .get()
                .uri("/auth/user-info") // Получение информации о пользователе
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .flatMap(userInfo -> {

                    cartItemDTO.setCustomer(userInfo.getUsername());
                    cartItemDTO.setCustomerId(userInfo.getId());

                    return webClient
                            .post()
                            .uri("/orders/create-order")
                            .bodyValue(cartItemDTO)
                            .retrieve()
                            .bodyToMono(CartDTO.class)
                            .thenReturn("redirect:/frontend/viewCart?productId=" + productId + "&token=" + token); // Перенаправление обратно на форму
                })
                .onErrorResume(ex -> {
                    logger.error("Ошибка при добавлении товара: {}", ex.getMessage(), ex);
                    return Mono.error(new RuntimeException("Не удалось добавить товар: " + ex.getMessage()));
                });
    }
}
