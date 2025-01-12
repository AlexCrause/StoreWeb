package com.example.OrderService.controller;

import com.example.OrderService.model.dto.OrderDTO;
import com.example.OrderService.model.dto.OrderItemRequestDTO;
import com.example.OrderService.model.dto.OrderRequestDTO;
import com.example.OrderService.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Проверка существования заказа
    @GetMapping("/checkOrderExistence")
    public Boolean checkOrderExistence(@RequestHeader("Authorization") String token) {
        return orderService.isOrderExistForUser(token);
    }

    @PostMapping("/addItem")
    public OrderDTO addItemsToOrder(@RequestBody @Valid OrderRequestDTO orderRequest) {
        String token = orderRequest.getToken();
        List<OrderItemRequestDTO> orderItems = orderRequest.getOrderItems();

        return orderService.addItemsToOrder(token, orderItems);
    }

    @PostMapping("/createOrder")
    public OrderDTO createOrder(@RequestBody @Valid OrderRequestDTO orderRequest) {
        String token = orderRequest.getToken(); // Получаем токен из запроса
        List<OrderItemRequestDTO> orderItems = orderRequest.getOrderItems();

        return orderService.createOrderWithItems(token, orderItems);
    }

    @GetMapping("/current")
    public OrderDTO getCurrentOrder(@RequestHeader("Authorization") String token) {
        return orderService.getCurrentOrderForUser(token);
    }
}
