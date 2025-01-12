package com.example.productservice.service;



import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.model.Product;
import com.example.productservice.model.dto.ProductBasicDTO;
import com.example.productservice.model.dto.ProductDetailedDTO;
import com.example.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    // Возвращает базовую информацию о всех продуктах
    public List<ProductBasicDTO> getAllBasicProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductBasicDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                ))
                .collect(Collectors.toList());
    }

    public List<ProductDetailedDTO> getAllDetailedProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDetailedDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock()
                ))
                .collect(Collectors.toList());
    }


    // Возвращает базовую информацию о продукте по ID
    public ProductBasicDTO getBasicProductById(UUID id) {
        Product product = getProductById(id);
        return new ProductBasicDTO(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }

    // Возвращает полную информацию о продукте по ID
    public ProductDetailedDTO getDetailedProductById(UUID productId) {
        Product product = getProductById(productId);
        return new ProductDetailedDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }


    // Создает новый продукт
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Обновляет продукт
    public Product updateProduct(UUID id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        return productRepository.save(product);
    }



    public void decreaseStock(UUID productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));
        product.setStock(product.getStock() - quantity);
        if (product.getStock() <= 0) {
            productRepository.delete(product); // Удаляем продукт, если на складе его больше нет
        } else {
            productRepository.save(product);
        }
    }
    // Удаляет продукт
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }


    // Вспомогательный метод для поиска продукта по ID с обработкой ошибки
    private Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
    }
}
