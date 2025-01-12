package com.example.OrderService.model.dto;

import java.util.List;
import java.util.UUID;

public class OrderDTO {

    private UUID id;
    private String name;// Уникальный идентификатор заказа
    private String status; // Статус заказа
    private List<OrderItemDTO> orderItems; // Список элементов заказа

    // Конструктор
    public OrderDTO(UUID id, String name, String status, List<OrderItemDTO> orderItems) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.orderItems = orderItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
