package com.example.shop.model;

public class CartItem {
    private Product product; // Товар
    private int quantity;    // Количество

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Метод для расчета общей стоимости
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}
