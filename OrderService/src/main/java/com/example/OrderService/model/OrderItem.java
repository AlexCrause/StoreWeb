package com.example.OrderService.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Уникальный идентификатор строки заказа

    @Column(nullable = false)
    private UUID productId; // Идентификатор продукта, связанный с ProductService

    //@Column(nullable = false)
    private String name; // Название продукта для удобства отображения

    @Column(nullable = false)
    private Integer quantity; // Количество

    @Column(nullable = false)
    private BigDecimal price; // Цена за единицу (изменено на BigDecimal)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order; // Связь с заказом

    // Вычисляемое поле для общей стоимости
    @Transient
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity)); // Умножаем BigDecimal
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getName() {
        return name;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) { // Проверка на отрицательную цену
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
