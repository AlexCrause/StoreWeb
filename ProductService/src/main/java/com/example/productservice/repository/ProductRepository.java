package com.example.productservice.repository;

import com.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Метод для поиска продуктов по имени
    List<Product> findByName(String name);

    // Метод для поиска продуктов по диапазону цен
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Метод для поиска продуктов по имени и диапазону цен
    List<Product> findByNameAndPriceBetween(String name, BigDecimal minPrice, BigDecimal maxPrice);

    // Метод для поиска продуктов по минимальной цене
    List<Product> findByPriceGreaterThanEqual(BigDecimal minPrice);

    // Метод для поиска продуктов по максимальной цене
    List<Product> findByPriceLessThanEqual(BigDecimal maxPrice);
}
