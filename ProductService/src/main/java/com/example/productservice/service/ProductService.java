package com.example.productservice.service;



import com.example.productservice.model.Product;
import com.example.productservice.model.dto.ProductBasicDTO;
import com.example.productservice.model.dto.ProductDetailedDTO;
import com.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Возвращает базовую информацию о всех продуктах
    public List<ProductBasicDTO> getAllBasicProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductBasicDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice() // Только базовая информация
                ))
                .collect(Collectors.toList());
    }

    // Возвращает полную информацию о всех продуктах
    public List<ProductDetailedDTO> getAllDetailedProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDetailedDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock() // Полная информация
                ))
                .collect(Collectors.toList());
    }

    // Возвращает базовую информацию о продукте по ID
    public ProductBasicDTO getBasicProductById(UUID id) {
        Product product = getProductById(id);
        return new ProductBasicDTO(
                product.getId(),
                product.getName(),
                product.getPrice() // Только базовая информация
        );
    }

    // Возвращает полную информацию о продукте по ID
    public ProductDetailedDTO getDetailedProductById(UUID id) {
        Product product = getProductById(id);
        return new ProductDetailedDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock() // Полная информация
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

    // Удаляет продукт
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    // Метод для уменьшения stock на 1 и удаления товара, если stock = 0
    public void decreaseStockAndDelete(UUID productId) {
        Product product = getProductById(productId);

        if (product.getStock() > 0) {
            product.setStock(product.getStock() - 1);
            productRepository.save(product);
        }

        if (product.getStock() == 0) {
            productRepository.delete(product);
        }
    }

    // Вспомогательный метод для поиска продукта по ID с обработкой ошибки
    private Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
    }
}
