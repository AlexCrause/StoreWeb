package com.example.productservice.service;

import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.model.Product;
import com.example.productservice.model.dto.ProductBasicDTO;
import com.example.productservice.model.dto.ProductDetailedDTO;
import com.example.productservice.model.dto.filter.ProductFilterDTO;
import com.example.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);


    /**
     * Фильтрует продукты по имени и диапазону цен.
     * @param filterDTO фильтрующие параметры (имя, минимальная и максимальная цена)
     * @return список отфильтрованных продуктов
     */
    public List<ProductDetailedDTO> filterProducts(ProductFilterDTO filterDTO) {
        // Декодирование имени, если оно задано
        String decodedName = filterDTO.getName() != null
                ? URLDecoder.decode(filterDTO.getName(), StandardCharsets.UTF_8)
                : null;
        // Установим декодированное имя обратно в DTO
        filterDTO.setName(decodedName);

        List<Product> products;

        // Фильтрация по имени и цене
        if (filterDTO.getName() != null && filterDTO.getMinPrice() != null && filterDTO.getMaxPrice() != null) {
            log.info("Фильтрация продуктов: имя = {}, minPrice = {}, maxPrice = {}",
                    filterDTO.getName(), filterDTO.getMinPrice(), filterDTO.getMaxPrice());
            products = productRepository.findByNameAndPriceBetween(filterDTO.getName(), filterDTO.getMinPrice(), filterDTO.getMaxPrice());
        } else if (filterDTO.getName() != null) {
            products = productRepository.findByName(filterDTO.getName());
        } else if (filterDTO.getMinPrice() != null && filterDTO.getMaxPrice() != null) {
            products = productRepository.findByPriceBetween(filterDTO.getMinPrice(), filterDTO.getMaxPrice());
        } else if (filterDTO.getMinPrice() != null) {
            products = productRepository.findByPriceGreaterThanEqual(filterDTO.getMinPrice());
        } else if (filterDTO.getMaxPrice() != null) {
            products = productRepository.findByPriceLessThanEqual(filterDTO.getMaxPrice());
        } else {
            products = productRepository.findAll(); // Возвращаем все продукты, если фильтры пустые
        }

        // Преобразуем сущности в DTO
        return products.stream()
                .map(this::convertToDetailedDTO)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует сущность продукта в DTO с детальной информацией.
     * @param product продукт
     * @return DTO с детальной информацией о продукте
     */
    private ProductDetailedDTO convertToDetailedDTO(Product product) {
        return new ProductDetailedDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }

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

    /**
     * Удаляет продукт по ID.
     * @param id идентификатор продукта для удаления
     * @throws ProductNotFoundException исключение, если продукт не найден
     */
    public void deleteProductById(UUID id) throws ProductNotFoundException {
        // Ищем продукт по ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        // Удаляем продукт
        productRepository.delete(product);
    }


    /**
     * Возвращает базовую информацию о всех продуктах.
     * @return список базовой информации о продуктах
     */
    public List<ProductBasicDTO> getAllBasicProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductBasicDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает полную информацию о всех продуктах.
     * @return список детальной информации о продуктах
     */
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


    /**
     * Возвращает базовую информацию о продукте по ID.
     * @param id идентификатор продукта
     * @return базовая информация о продукте
     */
    public ProductBasicDTO getBasicProductById(UUID id) {
        Product product = getProductById(id);
        return new ProductBasicDTO(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }

    /**
     * Возвращает полную информацию о продукте по ID.
     * @param productId идентификатор продукта
     * @return детальная информация о продукте
     */
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

    /**
     * Уменьшает количество товара на складе.
     * @param productId идентификатор продукта
     * @param quantity количество, на которое нужно уменьшить
     */
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


    /**
     * Вспомогательный метод для поиска продукта по ID с обработкой ошибки.
     * @param productId идентификатор продукта
     * @return найденный продукт
     * @throws IllegalArgumentException исключение, если продукт не найден
     */
    private Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
    }
}
