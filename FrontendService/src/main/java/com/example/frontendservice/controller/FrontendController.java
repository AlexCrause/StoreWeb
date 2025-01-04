//package com.example.frontendservice.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//@RestController
//public class FrontendController {
//
//    private final RestTemplate restTemplate;
//
//    public FrontendController(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    @GetMapping("/front/auth")
//    public String getAuth(@RequestParam String username, @RequestParam String password) {
//        // Пример запроса к AuthService через Gateway для аутентификации
//        String url = "http://localhost:8765/serviceAuth/authenticate?username=" + username + "&password=" + password;
//        return restTemplate.getForObject(url, String.class);
//    }
//
//    @GetMapping("/front/products")
//    public String getProducts() {
//        // Пример запроса к ProductService через Gateway для получения списка продуктов
//        String url = "http://localhost:8765/serviceProducts/products";
//        return restTemplate.getForObject(url, String.class);
//    }
//
//    // Вы можете добавлять другие методы для взаимодействия с другими микросервисами
//}
