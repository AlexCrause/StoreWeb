package com.example.OrderService.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CartItemDTO {

    @NotNull(message = "Product ID не должен быть null")
    private UUID productId;
    @Positive(message = "Количество должно быть положительным числом")
    private int quantity;
    @NotNull(message = "Цена не должна быть null")
    @Positive(message = "Цена должна быть положительным числом")
    private BigDecimal price;
    private String customer;
    private UUID customerId;


}
