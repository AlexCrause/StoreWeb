package com.example.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Добавьте реализацию проверки JWT (заглушка пока)
        filterChain.doFilter(request, response);
    }

//    private static final String SECRET_KEY = "your-secret-key"; // Замените на свой секретный ключ
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//        String jwt = null;
//        String username = null;
//
//        // Извлечение токена из заголовка Authorization
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            jwt = authHeader.substring(7); // Убираем "Bearer "
//            try {
//                // Декодирование токена с использованием новой библиотеки
//                Claims claims = Jwts.parserBuilder()
//                        .setSigningKey(SECRET_KEY.getBytes()) // Указание секретного ключа
//                        .build()
//                        .parseClaimsJws(jwt) // Метод для обработки JWT
//                        .getBody(); // Получаем содержимое токена
//
//                username = claims.getSubject(); // Получение имени пользователя из токена
//            } catch (SignatureException e) {
//                System.out.println("Ошибка в подписи токена: " + e.getMessage());
//            } catch (Exception e) {
//                System.out.println("Ошибка обработки токена: " + e.getMessage());
//            }
//        }
//
//        // Проверка пользователя и обновление контекста безопасности
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            // Создание объекта аутентификации
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    username,
//                    null,
//                    new ArrayList<>() // Роли (пустой список, если нет настроек)
//            );
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // Установка аутентификации в контекст
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        // Передача запроса дальше в цепочке фильтров
//        filterChain.doFilter(request, response);
//    }
}
