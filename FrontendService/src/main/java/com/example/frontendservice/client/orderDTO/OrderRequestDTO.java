package com.example.frontendservice.client.orderDTO;

import java.util.List;
import java.util.UUID;

public class OrderRequestDTO {

    private String token; // Токен пользователя для аутентификации
    private List<OrderItemRequestDTO> orderItems; // Список товаров для заказа

    // Конструктор
    public OrderRequestDTO(String token, List<OrderItemRequestDTO> orderItems) {
        this.token = token;
        this.orderItems = orderItems;
    }

    // Геттеры и сеттеры
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<OrderItemRequestDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequestDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
