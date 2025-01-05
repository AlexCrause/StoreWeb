package com.example.frontendservice.controller;

import com.example.frontendservice.client.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendAuthController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(FrontendAuthController.class);

    // Показать форму регистрации
    @GetMapping("/register")
    public String showRegisterPage() {
        return "users/register";
    }

    @GetMapping("/user/register")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            // Используем Gateway URL для регистрации
            restTemplate.postForObject(gatewayUrl + "/auth", user, Void.class);
            return "redirect:/users/login"; // Перенаправляем на страницу входа
        } catch (Exception e) {
            logger.error("Error during registration", e);
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "users/register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "users/login";
    }

    @GetMapping("/users/login")
    public String showLoginPage2() {
        return "users/login";
    }

    @PostMapping("/auth/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model) {
        try {
            // Логика для входа (например, аутентификация)
            // Если все хорошо, перенаправляем на главную страницу
            return "redirect:/products/list-products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка входа: " + e.getMessage());
            return "users/login"; // Вернуться на форму входа с ошибкой
        }
    }

}
