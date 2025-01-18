package com.example.productservice.model.dto.filter;

import java.math.BigDecimal;

public class ProductFilterDTO {

    private String name;         // Название продукта
    private BigDecimal minPrice; // Минимальная цена
    private BigDecimal maxPrice; // Максимальная цена


    public ProductFilterDTO() {
    }

    public ProductFilterDTO(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        this.name = name;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }


}
