package com.example.authservice.controller;

import com.example.authservice.model.Role;
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

import java.util.List;
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
            logger.info("Попытка аутентификации пользователя: {}", user.getUsername());
            boolean isAuthenticated = authService.authenticate(user.getUsername(), user.getPassword());
            if (isAuthenticated) {
                String token = jwtUtil.generateToken(user.getUsername());
                logger.info("Пользователь успешно прошел аутентификацию, токен сгенерирован.");
                return ResponseEntity.ok(token);
            } else {
                logger.warn("Неверные учетные данные для пользователя: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверное имя пользователя или пароль");
            }
        } catch (InvalidCredentialsException e) {
            logger.error("Ошибка аутентификации: Неверные учетные данные пользователя: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверное имя пользователя или пароль");
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при аутентификации: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Аутентификация не удалась");
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserDTO> getUserFromToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Неверный формат заголовка авторизации");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String token = jwtUtil.extractToken(authHeader);
        if (token != null && jwtUtil.isValidToken(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                User user = authService.findByUsername(username);
                if (user != null) {
                    UserDTO response = new UserDTO(user.getId(), user.getUsername(), user.getPassword());
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
            UserDTO response = new UserDTO(registeredUser.getId(), registeredUser.getUsername(), registeredUser.getPassword());
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

    @GetMapping("/user/role")
    public ResponseEntity<Boolean> getUserRoles(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Неверный формат заголовка авторизации");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Извлекаем токен из заголовка
        String token = jwtUtil.extractToken(authHeader);

        // Проверяем, валиден ли токен
        if (token != null && jwtUtil.isValidToken(token)) {
            // Извлекаем роль из токена
            String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));

            // Обработка логики для проверки роли и возврата соответствующего ответа
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok(true);  // Если роль ADMIN, возвращаем true
            } else {
                return ResponseEntity.ok(false);  // Если роль не ADMIN, возвращаем false
            }
        } else {
            logger.warn("Неверный токен");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



}
