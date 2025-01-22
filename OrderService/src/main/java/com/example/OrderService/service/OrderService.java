package com.example.OrderService.service;


import com.example.OrderService.dto.CartDTO;
import com.example.OrderService.dto.CartItemDTO;
import com.example.OrderService.model.Order;
import com.example.OrderService.model.OrderItem;
import com.example.OrderService.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;


    public CartDTO getItemsProductToCart(UUID productId) {
        List<Order> orders = orderRepository.findAllByProductId(productId);

        if (orders.isEmpty()) {
            return CartDTO.builder()
                    .items(List.of())
                    .totalPrice(BigDecimal.ZERO)
                    .build();
        }

        // Собираем все элементы из найденных заказов
        return orders.stream()
                .map(this::mapToResponseDTO)
                .reduce((dto1, dto2) -> {
                    dto1.getItems().addAll(dto2.getItems());
                    dto1.setTotalPrice(dto1.getTotalPrice().add(dto2.getTotalPrice()));
                    return dto1;
                })
                .orElse(CartDTO.builder()
                        .items(List.of())
                        .totalPrice(BigDecimal.ZERO)
                        .build());
    }

    public CartDTO mapToResponseDTO(Order order) {
        List<CartItemDTO> cartItems = order.getOrderItems().stream()
                .map(orderItem -> CartItemDTO.builder()
                        .productId(orderItem.getProductId())
                        .quantity(orderItem.getQuantity())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalPrice = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .filter(Objects::nonNull) // Убираем null-значения
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
                .items(cartItems)
                .totalPrice(totalPrice)
                .build();
    }



    public Order createOrderFromFrontend(List<CartItemDTO> cartItems, UUID customerId) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста. Невозможно создать заказ.");
        }

        // Создание нового заказа
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(customerId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING"); // Задаём статус по умолчанию
        order.setOrderItems(cartItems.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setId(UUID.randomUUID());
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    //orderItem.setName(item.getName());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList()));

        // Вычисление общей стоимости заказа
        BigDecimal totalOrderPrice = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalOrderPrice);

        // Сохранение заказа в базе данных
        return orderRepository.save(order);
    }



}
