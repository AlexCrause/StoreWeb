package com.example.frontendservice.controller;

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
public class FrontendReviewController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(FrontendReviewController.class);

    /**
     * Конструктор для инициализации WebClient с использованием URL шлюза.
     *
     * @param gatewayUrl Базовый URL шлюза.
     */
    public FrontendReviewController(@Value("${gateway.url}") String gatewayUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(gatewayUrl)
                .build();
    }

    /**
     * Отображает форму для добавления отзыва и загружает данные о продукте и существующих отзывах.
     *
     * @param productId UUID продукта, для которого добавляется отзыв.
     * @param token     JWT токен для аутентификации и авторизации.
     * @param model     Объект Model для передачи данных в представление.
     * @return Mono, возвращающее имя представления для отображения.
     */
    @GetMapping("/addForm")
    public Mono<String> showAddReviewFormAndReviews(@RequestParam UUID productId,
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
                            .uri("/reviews/product/{productId}", productId) // Получение отзывов о продукте
                            .headers(headers -> headers.set("Authorization", "Bearer " + token))
                            .retrieve()
                            .bodyToMono(ReviewResponseDTO[].class);
                })
                .flatMap(reviews -> {
                    model.addAttribute("reviews", reviews); // Добавление отзывов в модель
                    return Mono.just("products/add-review"); // Возврат имени представления
                })
                .onErrorResume(ex -> {
                    logger.error("Ошибка при загрузке данных: {}", ex.getMessage(), ex);
                    model.addAttribute("errorMessage", "Не удалось загрузить данные: " + ex.getMessage());
                    return Mono.just("products/error"); // Возврат представления с ошибкой в случае неудачи
                });
    }

    /**
     * Обрабатывает отправку формы добавления нового отзыва.
     * Получает информацию о пользователе из AuthService и отправляет отзыв в ReviewService.
     *
     * @param reviewCreateDTO DTO, содержащий данные отзыва из формы.
     * @param productId       UUID продукта, для которого добавляется отзыв.
     * @param token           JWT токен для аутентификации и авторизации.
     * @return Mono, возвращающее URL для перенаправления на форму добавления отзыва.
     */
    @PostMapping("/add/review")
    public Mono<String> addReviewWithoutTokenInDTO(
            @ModelAttribute ReviewCreateDTO reviewCreateDTO,
            @RequestParam("productId") UUID productId,
            @RequestParam("token") String token) {

        reviewCreateDTO.setProductId(productId); // Установка productId в DTO отзыва
        return webClient
                .get()
                .uri("/auth/user-info") // Получение информации о пользователе
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .flatMap(userInfo -> {
                    // Заполнение DTO отзыва данными о пользователе
                    reviewCreateDTO.setAuthor(userInfo.getUsername());
                    reviewCreateDTO.setUserId(userInfo.getId());

                    // Отправка отзыва в ReviewService
                    return webClient
                            .post()
                            .uri("/reviews")
                            .bodyValue(reviewCreateDTO)
                            .retrieve()
                            .bodyToMono(ReviewResponseDTO.class)
                            .thenReturn("redirect:/frontend/addForm?productId=" + productId + "&token=" + token); // Перенаправление обратно на форму
                })
                .onErrorResume(ex -> {
                    logger.error("Ошибка при добавлении отзыва: {}", ex.getMessage(), ex);
                    return Mono.error(new RuntimeException("Не удалось добавить отзыв: " + ex.getMessage()));
                });
    }
}
