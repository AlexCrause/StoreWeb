package com.example.frontendservice.controller;

import com.example.frontendservice.client.orderDTO.OrderDTO;
import com.example.frontendservice.client.orderDTO.OrderItemRequestDTO;
import com.example.frontendservice.client.orderDTO.OrderRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Controller
public class FrontendOrderController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(FrontendOrderController.class);

    public FrontendOrderController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(gatewayUrl).build();
    }

    @GetMapping("/products/addToCart")
    public String addToCart(
            @RequestParam("productId") UUID productId,
            @RequestParam("token") String token,
            Model model) {

        logger.info("Получен productId: {}", productId);

        if (productId == null) {
            logger.error("Ошибка: productId не может быть null");
            model.addAttribute("errorMessage", "Неверный productId");
            return "products/error";
        }

        String checkOrderUrl = "/orders/checkOrderExistence";

        Boolean orderExists = webClient.get()
                .uri(checkOrderUrl)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.TRUE.equals(orderExists)) {
            // Если заказ существует, добавляем товар в заказ
            OrderItemRequestDTO orderItem = new OrderItemRequestDTO(productId, 1);
            return addItemToExistingOrder(token, orderItem, model);
        } else {
            // Если заказа нет, создаем новый и добавляем товар
            return createNewOrderWithItem(token, productId, model);
        }
    }

    private String addItemToExistingOrder(String token, OrderItemRequestDTO orderItemRequest, Model model) {
        OrderRequestDTO orderRequest = new OrderRequestDTO(token, List.of(orderItemRequest));
        String addItemUrl = "/orders/addItem";

        OrderDTO order = webClient.post()
                .uri(addItemUrl)
                .header("Authorization", token)
                .bodyValue(orderRequest)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();

        model.addAttribute("order", order);
        model.addAttribute("successMessage", "Product added to order successfully!");

        return "products/authList-products";
    }

    private String createNewOrderWithItem(String token, UUID productId, Model model) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Authorization token cannot be null or blank");
        }

        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        OrderItemRequestDTO orderItemRequest = new OrderItemRequestDTO(productId, 1);
        OrderRequestDTO orderRequest = new OrderRequestDTO(token, List.of(orderItemRequest));
        String createOrderUrl = "/orders/createOrder";

        OrderDTO order = webClient.post()
                .uri(createOrderUrl)
                .header("Authorization", token)
                .bodyValue(orderRequest)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();

        model.addAttribute("order", order);
        model.addAttribute("successMessage", "New order created and product added!");

        return "products/authList-products";
    }

    @GetMapping("/cart/current")
    public String getCurrentOrder(@RequestParam("token") String token, Model model) {
        String currentOrderUrl = "/orders/current";

        OrderDTO order = webClient.get()
                .uri(currentOrderUrl)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .block();

        model.addAttribute("order", order);
        return "cart/cart";
    }
}
