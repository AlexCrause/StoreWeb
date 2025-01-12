package com.example.OrderService.model.dto;

import java.util.List;

public class OrderRequestDTO {

    private String token; // Токен пользователя для аутентификации
    private List<OrderItemRequestDTO> orderItems; // Список товаров для заказа


    public OrderRequestDTO(String token, List<OrderItemRequestDTO> orderItems) {
        this.token = token;
        this.orderItems = orderItems;
    }


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
