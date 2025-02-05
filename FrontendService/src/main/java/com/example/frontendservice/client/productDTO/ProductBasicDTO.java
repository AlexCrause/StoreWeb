package com.example.frontendservice.client.productDTO;


import java.math.BigDecimal;
import java.util.UUID;


public class ProductBasicDTO {

    private UUID id;
    private String name;
    private BigDecimal price;

    public ProductBasicDTO(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ProductBasicDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
