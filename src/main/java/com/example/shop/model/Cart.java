package com.example.shop.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Long, CartItem> items = new HashMap<>();

    public void addProduct(Product product, int quantity) {
        items.putIfAbsent(product.getId(), new CartItem(product, 0));
        CartItem item = items.get(product.getId());
        item.setQuantity(item.getQuantity() + quantity);
    }

    public void removeProduct(Long productId) {
        items.remove(productId);
    }

    public double getTotalPrice() {
        return items.values().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public Map<Long, CartItem> getItems() {
        return items;
    }
}
