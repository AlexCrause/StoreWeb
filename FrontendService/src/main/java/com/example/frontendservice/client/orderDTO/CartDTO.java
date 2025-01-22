package com.example.frontendservice.client.orderDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {

    private List<CartItemDTO> items;
    private BigDecimal totalPrice;

}
