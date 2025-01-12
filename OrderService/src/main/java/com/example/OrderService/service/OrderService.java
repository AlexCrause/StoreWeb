package com.example.OrderService.service;

import com.example.OrderService.exception.ProductUnavailableException;
import com.example.OrderService.model.Order;
import com.example.OrderService.model.OrderItem;
import com.example.OrderService.model.dto.*;
import com.example.OrderService.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для обработки заказов.
 * Содержит бизнес-логику для создания, обновления и управления заказами.
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // URL для шлюза, используется для взаимодействия с другими микросервисами
    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate;  // RestTemplate для взаимодействия с другими микросервисами
    private final OrderRepository orderRepository; // Репозиторий для работы с заказами

    /**
     * Конструктор для инициализации OrderService.
     *
     * @param gatewayUrl URL шлюза для взаимодействия с другими сервисами
     * @param orderRepository Репозиторий для работы с заказами
     */
    public OrderService(@Value("${gateway.url}") String gatewayUrl,
                        OrderRepository orderRepository,
                        RestTemplate restTemplate) {
        this.gatewayUrl = gatewayUrl;
        this.orderRepository = orderRepository; // Инициализируем репозиторий
        this.restTemplate = restTemplate;
    }

    private UUID getUserIdFromToken(String token) {
        String url = gatewayUrl + "/auth/user-info"; // Запрос на эндпоинт для получения информации о пользователе

        // Создаем заголовки и добавляем токен в заголовок Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Отправляем запрос с токеном в заголовке
            UserDTO userDTO = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO.class).getBody();
            if (userDTO != null && userDTO.getId() != null) {
                return userDTO.getId(); // Возвращаем ID пользователя, если он присутствует
            } else {
                throw new RuntimeException("Не удалось извлечь ID пользователя из ответа");
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Ошибка при запросе к микросервису аутентификации", e);
        }
    }

    /**
     * Проверка наличия активного заказа для пользователя.
     *
     * @param token Токен авторизации
     * @return true, если заказ существует
     */
    public boolean isOrderExistForUser(String token) {
        UUID userId = getUserIdFromToken(token);
        return orderRepository.existsByUserIdAndStatus(userId, "NEW"); // Проверка наличия заказа со статусом "NEW"
    }

    /**
     * Создание нового заказа с товарами для пользователя.
     * Проверяет наличие товаров и уменьшает их количество на складе.
     *
     * @param token Токен авторизации
     * @param orderItems Список товаров для добавления в заказ
     * @return DTO нового заказа
     */
    public OrderDTO createOrderWithItems(String token, List<OrderItemRequestDTO> orderItems) {
        UUID userId = getUserIdFromToken(token);
        log.info("Получен userId из токена: {}", userId);

        // Проверяем доступность товаров и уменьшаем их количество на складе
        for (OrderItemRequestDTO orderItemRequest : orderItems) {
            log.info("Проверка доступности товара: {}", orderItemRequest.getProductId());

            Boolean isAvailable = restTemplate.getForObject(gatewayUrl + "/products/{productId}/availability?quantity={quantity}",
                    Boolean.class, orderItemRequest.getProductId(), orderItemRequest.getQuantity());
            if (!isAvailable) {
                log.error("Продукт недоступен: {}", orderItemRequest.getProductId());
                throw new ProductUnavailableException("Продукт недоступен");
            }

            log.info("Товар доступен: {}", orderItemRequest.getProductId());

            // Уменьшаем количество товара на складе
            restTemplate.put(gatewayUrl + "/products/decreaseStock/{productId}?quantity={quantity}",
                    null, orderItemRequest.getProductId(), orderItemRequest.getQuantity());
        }

        // Получаем цены на все товары
        for (OrderItemRequestDTO orderItemRequest : orderItems) {
            BigDecimal price = restTemplate.getForObject(gatewayUrl + "/products/{productId}/price", BigDecimal.class, orderItemRequest.getProductId());
            orderItemRequest.setPrice(price);
        }

        // Создаем новый заказ и добавляем в него товары
        Order newOrder = new Order();
        newOrder.setStatus("NEW");
        newOrder.setUserId(userId);

        for (OrderItemRequestDTO item : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setName(item.getName());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            newOrder.getOrderItems().add(orderItem); // Добавляем товар в заказ
        }

        // Сохраняем заказ в базе данных
        Order savedOrder = orderRepository.save(newOrder);
        return convertToDTO(savedOrder);
    }

    /**
     * Добавление товаров в существующий заказ для пользователя.
     * Проверяет доступность товаров и уменьшает их количество на складе.
     *
     * @param token Токен авторизации
     * @param orderItems Список товаров для добавления в заказ
     * @return DTO обновленного заказа
     */
    public OrderDTO addItemsToOrder(String token, List<OrderItemRequestDTO> orderItems) {
        UUID userId = getUserIdFromToken(token);
        log.info("Получен userId из токена: {}", userId);

        Optional<Order> existingOrderOpt = orderRepository.findFirstByUserIdAndStatus(userId, "NEW");
        if (!existingOrderOpt.isPresent()) {
            throw new IllegalArgumentException("Активный заказ не найден для пользователя");
        }

        Order existingOrder = existingOrderOpt.get();
        log.info("Найден активный заказ для пользователя: {}", userId);

        // Проверяем доступность товаров и добавляем их в заказ
        for (OrderItemRequestDTO orderItemRequest : orderItems) {
            log.info("Проверка доступности товара: {}", orderItemRequest.getProductId());

            Boolean isAvailable = restTemplate.getForObject(gatewayUrl + "/products/{productId}/availability?quantity={quantity}",
                    Boolean.class, orderItemRequest.getProductId(), orderItemRequest.getQuantity());
            if (!isAvailable) {
                log.error("Продукт недоступен: {}", orderItemRequest.getProductId());
                throw new ProductUnavailableException("Продукт недоступен");
            }

            BigDecimal price = restTemplate.getForObject(gatewayUrl + "/products/{productId}/price", BigDecimal.class, orderItemRequest.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(orderItemRequest.getProductId());
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setPrice(price);

            existingOrder.getOrderItems().add(orderItem); // Добавляем товар в существующий заказ
        }

        // Сохраняем обновленный заказ
        Order savedOrder = orderRepository.save(existingOrder);
        return convertToDTO(savedOrder);
    }

    /**
     * Преобразует заказ в DTO.
     *
     * @param order Заказ
     * @return DTO заказа
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .collect(Collectors.toList()); // Преобразуем товары в DTO

        return new OrderDTO(order.getId(), order.getName(), order.getStatus(), itemDTOs); // Возвращаем DTO заказа
    }

    /**
     * Получение текущего заказа для пользователя.
     *
     * @param token Токен авторизации
     * @return DTO текущего заказа
     */
    public OrderDTO getCurrentOrderForUser(String token) {
        UUID userId = getUserIdFromToken(token);
        Optional<Order> orderOpt = orderRepository.findFirstByUserIdAndStatus(userId, "NEW");
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Текущий заказ не найден для пользователя");
        }
        return convertToDTO(orderOpt.get());
    }
}
