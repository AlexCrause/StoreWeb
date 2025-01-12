package com.example.authservice.util;

import com.example.authservice.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    // Генерация безопасного ключа для HMAC-SHA256
    private final Key secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    private final UserRepository userRepository;

    // Внедрение зависимостей
    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String username) {
        // Находим пользователя по имени
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Проверяем, что у пользователя есть роли
        Set<Role> roles = user.getRoles();
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("User has no roles assigned");
        }

        // Получаем первую роль пользователя (если их несколько)
        String role = roles.iterator().next().getName();

        return Jwts.builder()
                .setSubject(username)  // Subject — это имя пользователя
                .claim("role", role)  // Добавляем одну роль в токен как claim
                .setIssuedAt(new Date())  // Время выдачи токена — текущее время
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // Время истечения срока действия (1 час)
                .signWith(secretKey)  // Подписание JWT с использованием безопасного ключа
                .compact();  // Создание JWT
    }


    // Извлечение имени пользователя из токена
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Извлечение роли из токена
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Извлечение времени истечения срока действия из токена
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Извлечение любого другого утверждения (например, имени пользователя)
    public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    // Извлечение всех утверждений из токена
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Использование безопасного ключа
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Проверка, истек ли срок действия токена
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Валидация токена
    public boolean isValidToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Получение имени пользователя из токена
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }

    // Извлечение токена из заголовка Authorization
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // Убираем "Bearer " и возвращаем только токен
        }
        return null;  // Если заголовок не начинается с "Bearer ", возвращаем null
    }

    // Функциональный интерфейс для извлечения утверждений
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
