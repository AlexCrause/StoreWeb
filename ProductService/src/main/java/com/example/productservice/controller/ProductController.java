package com.example.productservice.controller;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.productservice.model.Product;
import com.example.productservice.model.dto.ProductBasicDTO;
import com.example.productservice.model.dto.ProductDetailedDTO;
import com.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")

public class ProductController {


    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Возвращает список продуктов
    @GetMapping
    public ResponseEntity<List<?>> getAllProducts(
            @RequestHeader(name = "Authorization", required = false) String token) {
        if (isValidToken(token)) {
            return ResponseEntity.ok(productService.getAllDetailedProducts()); // Полная информация
        }
        return ResponseEntity.ok(productService.getAllBasicProducts()); // Базовая информация
    }

    // Возвращает продукт по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable UUID id,
            @RequestHeader(name = "Authorization", required = false) String token) {
        if (isValidToken(token)) {
            return ResponseEntity.ok(productService.getDetailedProductById(id)); // Полная информация
        }
        return ResponseEntity.ok(productService.getBasicProductById(id)); // Базовая информация
    }

    // Создает новый продукт
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        // Для ответа использовать DTO, а не сущность
        return ResponseEntity
                .created(URI.create("/products/" + createdProduct.getId()))
                .body(new ProductDetailedDTO(
                        createdProduct.getId(),
                        createdProduct.getName(),
                        createdProduct.getDescription(),
                        createdProduct.getPrice(),
                        createdProduct.getStock()
                ));
    }

    // Обновляет информацию о продукте
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        // Для ответа использовать DTO, а не сущность
        return ResponseEntity.ok(new ProductDetailedDTO(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getStock()
        ));
    }

    // Удаляет продукт
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Уменьшает stock и удаляет товар
    @PutMapping("/decreaseAndDelete/{id}")
    public ResponseEntity<Void> decreaseStockAndDelete(@PathVariable UUID id) {
        productService.decreaseStockAndDelete(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        return true;
    }
}
