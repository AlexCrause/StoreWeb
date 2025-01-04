package com.example.frontendservice.controller;


import com.example.frontendservice.client.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Controller
public class FrontendController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/products")
    public String listProducts(Model model) {
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                gatewayUrl + "/api/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {});
        List<Product> products = response.getBody();
        model.addAttribute("products", products);
        return "list-products";
    }


    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product) {
        restTemplate.postForObject(gatewayUrl + "/api/products", product, Product.class);
        return "redirect:/products";
    }

    // Метод для удаления товара с уменьшением stock на 1
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        restTemplate.postForObject(gatewayUrl + "/api/products/decreaseAndDelete/" + id, null, Void.class);
        return "redirect:/products";
    }
}
