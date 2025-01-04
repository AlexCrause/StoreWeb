package com.example.OrderService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "AuthService")
public interface AuthServiceClient {

    @GetMapping("/api/v1/users/{userId}/validate")
    boolean isValidUser(@PathVariable("userId") UUID userId);
}
