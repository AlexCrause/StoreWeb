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
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
//@RequestMapping("/products")
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

    /**
     * Обрабатывает запрос на получение списка продуктов для гостей.
     * Данные получаются без использования токена авторизации.
     * В случае успеха добавляет список продуктов в модель и указывает, что пользователь не авторизован.
     * В случае ошибки добавляет сообщение об ошибке в модель.
     *
     * @param model объект Model для передачи данных в представление
     * @return представление "products/list-products" с данными для отображения
     */
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

    /**
     * Метод отображает форму для добавления нового продукта.
     * Пользователь должен быть авторизован (администратор), чтобы использовать эту форму.
     *
     * @param token токен администратора
     * @param model объект Model для передачи данных в представление
     * @return представление "products/add-product" с формой для добавления нового товара
     */
    @GetMapping("products/add")
    public String showAddProductForm(@RequestParam("token") String token, Model model) {
        logger.info("Запрос на отображение формы добавления нового товара.");
        model.addAttribute("token", token);
        model.addAttribute("product", new ProductDetailedDTO());
        return "products/add-product";
    }

    /**
     * Метод обрабатывает отправку формы для добавления нового продукта.
     * Продукт добавляется с использованием токена администратора.
     *
     * @param product объект DTO с данными нового продукта
     * @param token токен администратора
     * @return перенаправление на страницу со списком продуктов
     */
    @PostMapping("products/add")
    public Mono<String> addProduct(
            @ModelAttribute ProductDetailedDTO product,
            @RequestParam("token") String token) {
        logger.info("Отправка запроса на добавление нового товара: {}", product);

        return webClient.post()
                .uri("/products")
                .headers(headers -> headers.set("Authorization", "Bearer " + token))
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(unused -> logger.info("Продукт успешно добавлен."))
                .thenReturn("redirect:/products/authList-products?token=" + token)
                .onErrorResume(e -> {
                    logger.error("Ошибка при добавлении продукта: {}", e.getMessage(), e);
                    return Mono.just("redirect:/products?error=failed-to-add-product");
                });
    }

    /**
     * Обрабатывает запрос на отображение списка продуктов для авторизованного пользователя.
     * Проверяет роль пользователя (администратор или нет) и сохраняет информацию о роли в сессии.
     * В случае ошибки добавляет сообщение об ошибке в модель.
     *
     * @param token токен авторизации
     * @param model объект Model для передачи данных в представление
     * @param session объект WebSession для работы с сессией
     * @return представление "products/authList-products" с данными для отображения
     */
    @GetMapping("/products/authList-products")
    public Mono<String> showAuthenticatedProductList(
            @RequestParam("token") String token,
            Model model,
            WebSession session) {
        logger.info("Запрос списка продуктов для авторизованного пользователя с токеном: {}", token);

        return isAdmin(token)
                .flatMap(isAdminFromService -> {
                    logger.info("Пользователь администратор: {}", isAdminFromService);

                    // Сохраняем информацию о роли в сессии для будущих запросов
                    session.getAttributes().put("isAdmin", isAdminFromService);

                    // Явно сохраняем сессию
                    return session.save()
                            .then(webClient.get()
                                    .uri("/products") // Предположим, что этот URI вернет список продуктов
                                    .headers(headers -> headers.set("Authorization", "Bearer " + token))
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<List<ProductDetailedDTO>>() {})
                                    .doOnNext(products -> {
                                        logger.info("Получено {} продуктов для авторизованного пользователя.", products.size());
                                        model.addAttribute("products", products);
                                        model.addAttribute("isUserAuthenticated", true);
                                        model.addAttribute("token", token);
                                        model.addAttribute("isAdmin", isAdminFromService); // Добавляем информацию о роли администратора
                                    })
                                    .thenReturn("products/authList-products"));
                })
                .onErrorResume(e -> {
                    logger.error("Ошибка при получении списка продуктов: {}", e.getMessage(), e);
                    model.addAttribute("errorMessage", "Не удалось загрузить продукты.");
                    model.addAttribute("isUserAuthenticated", true);
                    model.addAttribute("token", token);
                    return Mono.just("products/error");
                });
    }

    /**
     * Проверяет, является ли пользователь с указанным токеном администратором.
     * Выполняет запрос к сервису авторизации и ожидает true/false в ответе.
     * В случае ошибки возвращает false, предполагая, что пользователь не является администратором.
     *
     * @param token токен авторизации
     * @return Mono<Boolean> true, если пользователь администратор; false в противном случае
     */
    private Mono<Boolean> isAdmin(String token) {
        logger.info("Проверка, является ли пользователь с токеном {} администратором.", token);
        return webClient.get()
                .uri("/auth/user/role") // Предположим, что есть эндпоинт для получения роли пользователя
                .headers(headers -> headers.set("Authorization", "Bearer " + token))
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false); // Возвращаем false, если роль не найдена или ошибка при получении роли
    }

}

