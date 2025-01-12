package com.example.frontendservice.controller;

import com.example.frontendservice.client.orderDTO.OrderDTO;
import com.example.frontendservice.client.orderDTO.OrderItemRequestDTO;
import com.example.frontendservice.client.orderDTO.OrderRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Controller
public class FrontendOrderController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(FrontendOrderController.class);

    public FrontendOrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

        // Проверяем существование заказа
        String checkOrderUrl = gatewayUrl + "/orders/checkOrderExistence";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(checkOrderUrl,
                HttpMethod.GET, entity, Boolean.class);

        response.getBody();
        if (response.getBody()) {
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

        String addItemUrl = gatewayUrl + "/orders/addItem";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<OrderRequestDTO> entity = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<OrderDTO> response = restTemplate.exchange(addItemUrl,
                HttpMethod.POST, entity, OrderDTO.class);

        response.getBody();
        model.addAttribute("order", response.getBody());
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

        String createOrderUrl = gatewayUrl + "/orders/createOrder";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<OrderRequestDTO> entity = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<OrderDTO> response = restTemplate.exchange(createOrderUrl,
                HttpMethod.POST, entity, OrderDTO.class);

        response.getBody();
        model.addAttribute("order", response.getBody());
        model.addAttribute("successMessage", "New order created and product added!");

        return "products/authList-products";
    }

    @GetMapping("/cart/current")
    public String getCurrentOrder(@RequestParam("token") String token, Model model) {
        String currentOrderUrl = gatewayUrl + "/orders/current";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<OrderDTO> response = restTemplate.exchange(currentOrderUrl, HttpMethod.GET, entity, OrderDTO.class);

        response.getBody();
        model.addAttribute("order", response.getBody());
        return "cart/cart";

    }
}
