package com.example.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Генерация безопасного ключа для HMAC-SHA256
    private Key secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    // Генерация JWT
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // Subject — это имя пользователя
                .setIssuedAt(new Date())  // Время выдачи токена — текущее время
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // Время истечения срока действия (1 час)
                .signWith(secretKey)  // Подписание JWT с использованием безопасного ключа
                .compact();  // Создание JWT
    }

    // Извлечение имени пользователя из токена
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Извлечение времени истечения срока действия из токена
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Извлечение любого другого утверждения (например, имени пользователя)
    private <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
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
