package com.example.OrderService.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemRequestDTO {

    private UUID productId;
    private String name;// Идентификатор товара
    private Integer quantity; // Количество товара для заказа
    private BigDecimal price; // Цена товара

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
