package com.example.productservice.controller;


import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.model.dto.filter.ProductFilterDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.productservice.model.Product;
import com.example.productservice.model.dto.ProductDetailedDTO;
import com.example.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер для обработки запросов, связанных с продуктами.
 * Включает методы для фильтрации, получения, создания, обновления и удаления продуктов.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {


    private final ProductService productService;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    /**
     * Фильтрует продукты по заданным параметрам: название, минимальная и максимальная цена.
     *
     * @param name      Название продукта (необязательный параметр).
     * @param minPrice  Минимальная цена (необязательный параметр).
     * @param maxPrice  Максимальная цена (необязательный параметр).
     * @param token     Токен авторизации пользователя.
     * @return ResponseEntity с результатами фильтрации продуктов или ошибкой.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<ProductDetailedDTO>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String token) {
        if (isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ProductFilterDTO filterDTO = new ProductFilterDTO(name, minPrice, maxPrice);
            List<ProductDetailedDTO> filteredProducts = productService.filterProducts(filterDTO);
            return ResponseEntity.ok(filteredProducts);
        } catch (Exception e) {
            log.error("Ошибка при фильтрации продуктов: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ответ при ошибке фильтрации
        }
    }

    /**
     * Возвращает список всех продуктов, в зависимости от авторизации пользователя.
     * Если токен действителен, возвращается полная информация о товарах.
     * В противном случае — базовая информация.
     *
     * @param token Токен авторизации.
     * @return ResponseEntity с соответствующими данными продуктов.
     */
    @GetMapping
    public ResponseEntity<List<?>> getAllProducts(
            @RequestHeader(name = "Authorization", required = false) String token) {
        if (isValidToken(token)) {
            return ResponseEntity.ok(productService.getAllDetailedProducts()); // Полная информация
        }
        return ResponseEntity.ok(productService.getAllBasicProducts()); // Базовая информация
    }

    /**
     * Обрабатывает запрос на создание нового продукта.
     *
     * @param authorization Токен авторизации
     * @param productDTO    DTO с данными нового продукта
     * @return ResponseEntity с информацией о созданном продукте
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestHeader("Authorization") String authorization,
                                           @RequestBody ProductDetailedDTO productDTO) {
        if (!isValidToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
        }

        // Преобразование и создание продукта через сервис
        Product createdProduct = productService.createProduct(productDTO);

        // Возвращаем ответ с созданным продуктом
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


    /**
     * Обрабатывает запрос на обновление информации о продукте.
     * <p>
     * Этот метод принимает данные продукта в формате DTO, передает их в сервис
     * для обновления информации и возвращает обновленный объект в формате DTO.
     *
     * @param id         идентификатор продукта, который необходимо обновить
     * @param productDTO объект DTO с обновленными данными продукта
     * @return ResponseEntity с обновленным объектом ProductDetailedDTO и статусом 200 OK
     */

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateProduct(@RequestHeader("Authorization") String authorization,
                                           @PathVariable UUID id,
                                           @RequestBody ProductDetailedDTO productDTO) {
        if (!isValidToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
        }
        // Вызываем метод сервиса, передавая DTO
        ProductDetailedDTO updatedProductDTO = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProductDTO);
    }

    /**
     * Удаляет продукт по его идентификатору.
     *
     * @param authorization Токен авторизации.
     * @param id           Идентификатор продукта.
     * @return Ответ с результатом удаления.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("Authorization") String authorization,
                                           @PathVariable UUID id) {
        // Проверка токена
        if (!isValidToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
        }

        try {
            productService.deleteProductById(id); // Удаляем продукт только по ID
            return ResponseEntity.noContent().build(); // Успешное удаление
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found"); // Продукт не найден
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred"); // Прочие ошибки
        }
    }


    /**
     * Возвращает продукт по его идентификатору.
     *
     * @param id     Идентификатор продукта.
     * @param token  Токен авторизации.
     * @return Информацию о продукте.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable UUID id,
            @RequestHeader(name = "Authorization") String token) {
        if (isValidToken(token)) {
            return ResponseEntity.ok(productService.getDetailedProductById(id)); // Полная информация
        }
        return ResponseEntity.ok(productService.getBasicProductById(id)); // Базовая информация
    }


//    /**
//     * Проверяет доступность продукта по его количеству.
//     *
//     * @param productId Идентификатор продукта.
//     * @param quantity  Необходимое количество товара.
//     * @return True, если продукт доступен в достаточном количестве, иначе False.
//     */
//    @GetMapping("/{productId}/availability")
//    public ResponseEntity<Boolean> checkAvailability(
//            @PathVariable UUID productId,
//            @RequestParam Integer quantity) {
//        log.info("Получен запрос на проверку доступности товара с ID: {} и количеством: {}", productId, quantity);
//        ProductDetailedDTO product = productService.getDetailedProductById(productId);
//        boolean isAvailable = product.getStock() >= quantity;
//        log.info("Доступность товара: {}", isAvailable);
//        return ResponseEntity.ok(isAvailable);
//    }


    /**
     * Возвращает цену товара по его идентификатору.
     *
     * @param id Идентификатор продукта.
     * @return Цена продукта.
     */
    @GetMapping("/{id}/price")
    public ResponseEntity<BigDecimal> getPrice(@PathVariable UUID id) {
        ProductDetailedDTO product = productService.getDetailedProductById(id);
        BigDecimal price = product.getPrice();
        return ResponseEntity.ok(price);
    }


    /**
     * Уменьшает количество товара на складе.
     *
     * @param id       Идентификатор продукта.
     * @param quantity Количество, на которое нужно уменьшить запас.
     * @return Ответ без содержимого, статус 204 No Content.
     */
    @PutMapping("/decreaseStock/{id}")
    public ResponseEntity<Void> decreaseStock(@PathVariable UUID id, @RequestParam int quantity) {
        productService.decreaseStock(id, quantity);
        return ResponseEntity.noContent().build();
    }


    private boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        return true;
    }
}
