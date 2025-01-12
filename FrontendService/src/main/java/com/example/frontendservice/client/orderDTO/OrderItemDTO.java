package com.example.frontendservice.client.orderDTO;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemDTO {

    private UUID id; // Уникальный идентификатор строки заказа
    private UUID productId; // Идентификатор продукта
    private String name; // Название продукта
    private Integer quantity; // Количество
    private BigDecimal price; // Цена за единицу
    private BigDecimal totalPrice; // Общая стоимость (вычисляется на основе quantity * price)

    // Конструктор для заполнения данных
    public OrderItemDTO(UUID id, UUID productId, String name, Integer quantity, BigDecimal price) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price.multiply(BigDecimal.valueOf(quantity)); // Вычисляем общую стоимость
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.totalPrice = this.price.multiply(BigDecimal.valueOf(this.quantity)); // Пересчитываем общую стоимость
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        this.totalPrice = this.price.multiply(BigDecimal.valueOf(this.quantity)); // Пересчитываем общую стоимость
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
