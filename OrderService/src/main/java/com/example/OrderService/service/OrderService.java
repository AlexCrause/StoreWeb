package com.example.OrderService.service;

import com.example.OrderService.client.AuthServiceClient;
import com.example.OrderService.client.ProductServiceClient;
import com.example.OrderService.exception.InvalidUserException;
import com.example.OrderService.exception.ProductUnavailableException;
import com.example.OrderService.model.Order;
import com.example.OrderService.model.OrderItem;
import com.example.OrderService.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final AuthServiceClient authServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;

    public OrderService(AuthServiceClient authServiceClient, ProductServiceClient productServiceClient, OrderRepository orderRepository) {
        this.authServiceClient = authServiceClient;
        this.productServiceClient = productServiceClient;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(UUID userId, List<OrderItem> items) {
        logger.info("Starting order creation for user: {}", userId);

        // Проверка пользователя через AuthService
        if (!authServiceClient.isValidUser(userId)) {
            logger.error("Invalid user: {}", userId);
            throw new InvalidUserException("User ID " + userId + " is invalid");
        }

        // Проверка наличия продуктов через ProductService
        for (OrderItem item : items) {
            if (!productServiceClient.isProductAvailable(item.getProductId(), item.getQuantity())) {
                logger.error("Product unavailable: {} (quantity: {})", item.getProductId(), item.getQuantity());
                throw new ProductUnavailableException("Product ID " + item.getProductId() + " is not available");
            }
        }

        // Уменьшение остатков товаров
        for (OrderItem item : items) {
            productServiceClient.decreaseProductStock(item.getProductId(), item.getQuantity());
            logger.info("Decreased stock for product: {} (quantity: {})", item.getProductId(), item.getQuantity());
        }

        // Создание нового заказа
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderItems(items);
        order.setStatus("NEW"); // Устанавливаем статус заказа

        // Сохранение заказа в базу данных
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully: {}", savedOrder.getId());

        return savedOrder;
    }
}
