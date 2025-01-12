package com.example.productservice.model.dto;

import java.util.UUID;

public class OrderItemRequestDTO {

    private UUID productId; // Идентификатор товара
    private Integer quantity; // Количество товара для заказа

    // Конструктор
    public OrderItemRequestDTO(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
