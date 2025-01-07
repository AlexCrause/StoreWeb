package com.example.frontendservice.controller;
import com.example.frontendservice.client.userDTO.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/auth")
public class FrontendAuthController {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(FrontendAuthController.class);

    public FrontendAuthController(@Value("${gateway.url}") String gatewayUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(gatewayUrl)
                .build();
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "users/register";
    }

    @GetMapping("/register/user")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "users/register";
    }

    @PostMapping("/register/user")
    public Mono<String> registerUser(@ModelAttribute UserDTO user, Model model) {
        return webClient.post()
                .uri("/auth/register")  // Используем другой URI для регистрации
                .bodyValue(user)
                .retrieve()
                .toBodilessEntity()
                .map(response -> "redirect:/auth/login")  // Перенаправление после успешной регистрации
                .onErrorResume(e -> {
                    logger.error("Ошибка при регистрации", e);
                    model.addAttribute("errorMessage", "Ошибка регистрации: " + e.getMessage());
                    return Mono.just("users/register");
                });
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public Mono<String> loginUser(@ModelAttribute UserDTO user, Model model) {

        // Отправляем POST-запрос на сервер для аутентификации
        return webClient.post()  // Используем WebClient для отправки POST-запроса
                .uri("/auth/login")  // Указываем URL ресурса на сервере для аутентификации
                .bodyValue(user)  // Отправляем объект с данными пользователя (имя и пароль)
                .retrieve()  // Выполняем запрос
                .bodyToMono(String.class) // Ожидаем, что сервер вернет токен в виде строки
                .map(token -> {
                    // Добавляем токен в модель и перенаправляем на страницу /auth/dashboard
                    model.addAttribute("token", token);
                    return "redirect:/auth/dashboard?token=" + token;
                })
                .onErrorResume(e -> {
                    // Обработка ошибок при аутентификации
                    logger.error("Login error: {}", e.getMessage());
                    model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
                    return Mono.just("users/login");  // Возвращаем страницу входа в случае ошибки
                });
    }

    @GetMapping("/dashboard")
    public Mono<String> showDashboard(@RequestParam("token") String token, Model model) {

        // Добавляем токен в модель
        model.addAttribute("token", token);

        // Отправляем GET-запрос для получения информации о пользователе, передавая токен в заголовке
        return webClient.get()
                .uri("/auth/user-info")  // Указываем URL для получения информации о пользователе
                .header("Authorization", "Bearer " + token)  // Добавляем токен в заголовок Authorization
                .retrieve()  // Выполняем запрос
                .bodyToMono(UserDTO.class)  // Ожидаем, что сервер вернет данные пользователя в виде объекта UserDTO
                .map(user -> {
                    // Добавляем данные пользователя в модель и перенаправляем на страницу /auth/show/dashboard
                    model.addAttribute("user", user.getUsername());
                    return "users/dashboard";  // Перенаправление на страницу с детальной информацией о пользователе
                })
                .onErrorResume(e -> {
                    // Обработка ошибок при получении информации о пользователе
                    logger.error("Error fetching user info: {}", e.getMessage());
                    model.addAttribute("errorMessage", "Failed to fetch user info: " + e.getMessage());
                    return Mono.just("error");  // Возвращаем страницу ошибки в случае неудачи
                });
    }
}
