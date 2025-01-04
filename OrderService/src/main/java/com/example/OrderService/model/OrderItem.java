package com.example.OrderService.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Уникальный идентификатор строки заказа

    @Column(nullable = false)
    private UUID productId; // Идентификатор продукта, связанный с ProductService

    @Column(nullable = false)
    private String productName; // Название продукта для удобства отображения

    @Column(nullable = false)
    private Integer quantity; // Количество

    @Column(nullable = false)
    private Double price; // Цена за единицу

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // Связь с заказом

    // Вычисляемое поле для общей стоимости
    @Transient
    public Double getTotalPrice() {
        return quantity * price;
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

}
