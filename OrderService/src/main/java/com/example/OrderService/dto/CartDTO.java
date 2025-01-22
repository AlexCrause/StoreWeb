package com.example.OrderService.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartDTO {

    private List<CartItemDTO> items;
    private BigDecimal totalPrice;

}
