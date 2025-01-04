package com.example.OrderService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "ProductService")
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{productId}/check-availability")
    boolean isProductAvailable(@PathVariable("productId") UUID productId, int quantity);

    @PostMapping("/api/v1/products/{productId}/decrease-stock")
    void decreaseProductStock(@PathVariable("productId") UUID productId, @RequestParam("quantity") int quantity);
}
