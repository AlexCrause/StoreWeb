package com.example.authservice.controller;

import com.example.authservice.model.dto.UserDTO;
import com.example.authservice.model.User;
import com.example.authservice.service.AuthService;
import com.example.authservice.util.JwtUtil;
import com.example.authservice.exception.InvalidCredentialsException;
import com.example.authservice.exception.UserNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody UserDTO user) {
        try {
            logger.info("Attempting to authenticate user: {}", user.getUsername());
            boolean isAuthenticated = authService.authenticate(user.getUsername(), user.getPassword());
            if (isAuthenticated) {
                String token = jwtUtil.generateToken(user.getUsername());
                logger.info("User authenticated successfully, token generated.");
                return ResponseEntity.ok(token);
            } else {
                logger.warn("Invalid credentials for user: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (InvalidCredentialsException e) {
            logger.error("Authentication error: Invalid credentials for user: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            logger.error("Unexpected error during authentication: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserDTO> getUserFromToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Invalid Authorization header format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String token = jwtUtil.extractToken(authHeader);
        if (token != null && jwtUtil.isValidToken(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                User user = authService.findByUsername(username);
                if (user != null) {
                    UserDTO response = new UserDTO(user.getUsername(), user.getPassword());
                    logger.info("User info retrieved for username: {}", username);
                    return ResponseEntity.ok(response);
                } else {
                    logger.warn("User not found for username: {}", username);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } catch (UserNotFoundException e) {
                logger.error("Error retrieving user: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } catch (Exception e) {
                logger.error("Unexpected error during user info retrieval: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            logger.warn("Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody User user) {
        logger.info("Registering user: {}", user.getUsername());
        try {
            User registeredUser = authService.registerUser(user);
            UserDTO response = new UserDTO(registeredUser.getUsername(), registeredUser.getPassword());
            logger.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Unexpected error during registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        try {
            User user = authService.findById(userId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            logger.warn("User not found for ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error retrieving user by ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable UUID userId) {
        try {
            authService.deleteById(userId);
            logger.info("User with ID {} deleted successfully", userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
