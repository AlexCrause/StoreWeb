package com.example.frontendservice.controller;

import com.example.frontendservice.client.ProductServiceClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final ProductServiceClient productServiceClient;

    public MainController(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    @GetMapping("/products")
    public String getProducts(Model model) {
        model.addAttribute("products", productServiceClient.getProducts());
        return "products"; // Путь к шаблону resources/templates/products.html
    }
}
