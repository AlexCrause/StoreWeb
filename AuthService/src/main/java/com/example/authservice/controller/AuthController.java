package com.example.authservice.controller;

import com.example.authservice.model.User;
import com.example.authservice.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        logger.info("Registering user: {}", user.getUsername());
        try {
            User registeredUser = authService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
