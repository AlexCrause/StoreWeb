package com.example.productservice.service;



import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.model.Product;
import com.example.productservice.model.dto.ProductBasicDTO;
import com.example.productservice.model.dto.ProductDetailedDTO;
import com.example.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    /**
     * Создает новый продукт, преобразуя DTO в сущность.
     *
     * @param productDTO DTO с данными продукта
     * @return созданный продукт
     */
    public Product createProduct(ProductDetailedDTO productDTO) {
        // Преобразование DTO в сущность Product
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());

        // Сохранение продукта в репозитории
        return productRepository.save(product);
    }


    /**
     * Обновляет информацию о продукте в базе данных.
     * <p>
     * Метод выполняет следующие действия:
     * 1. Преобразует входящий объект DTO в сущность `Product`.
     * 2. Использует репозиторий для сохранения обновленной сущности в базе данных.
     * 3. Преобразует обновленную сущность обратно в объект DTO для возврата.
     * <p>
     * Аннотация {@code @Transactional} гарантирует, что все операции с базой данных
     * выполняются в одной транзакции, и при возникновении исключений изменения будут откатаны.
     *
     * @param id         идентификатор продукта, который необходимо обновить
     * @param productDTO объект DTO с обновленными данными продукта
     * @return обновленный объект ProductDetailedDTO
     */
    @Transactional
    public ProductDetailedDTO updateProduct(UUID id, ProductDetailedDTO productDTO) {
        // Преобразуем DTO в сущность
        Product product = new Product();
        product.setId(id);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());

        // Обновляем продукт в базе данных
        Product updatedProduct = productRepository.save(product);

        // Преобразуем обновленную сущность обратно в DTO для ответа
        return new ProductDetailedDTO(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getStock()
        );
    }


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
