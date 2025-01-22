package com.example.OrderService.controller;

import com.example.OrderService.dto.CartDTO;
import com.example.OrderService.dto.CartItemDTO;
import com.example.OrderService.model.Order;
import com.example.OrderService.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);


    @PostMapping("/create-order")
    public ResponseEntity<CartDTO> createOrder(@Valid @RequestBody CartItemDTO cartItemDTO) {
        if (cartItemDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Создаём заказ на основе полученного DTO
            Order order = orderService.createOrderFromFrontend(List.of(cartItemDTO), cartItemDTO.getCustomerId());

            // Преобразуем заказ обратно в CartDTO для ответа на фронтенд
            CartDTO cartDTO = orderService.mapToResponseDTO(order);

            return ResponseEntity.ok(cartDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Обработка ошибок (например, пустой заказ)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Обработка общих ошибок
        }
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getCartByProduct(@PathVariable UUID productId) {
        try {
            CartDTO cartDTO = orderService.getItemsProductToCart(productId);
            if (cartDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Корзина не найдена");
            }
            return ResponseEntity.ok(cartDTO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + ex.getMessage());
        }
    }

}
