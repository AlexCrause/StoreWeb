package com.example.productservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductDetailedDTO {

    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;


    // Конструктор
    public ProductDetailedDTO(UUID productId, String name, String description, BigDecimal price, int stock) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;

    }

    public ProductDetailedDTO() {
    }

    public UUID getId() {
        return productId;
    }

    public void setId(UUID id) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
