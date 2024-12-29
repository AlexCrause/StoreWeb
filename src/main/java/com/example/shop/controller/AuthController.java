package com.example.shop.controller;

import ch.qos.logback.core.model.Model;
import com.example.shop.model.User;
import com.example.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

//    @GetMapping("/login")
//    public String login() {
//        return "login"; // Возвращает шаблон login.html
//    }

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register() {
        return "register"; // Возвращает шаблон register.html
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // Обычно пароли следует хэшировать.

        userService.registerUser(user);
        model.addText("success");

        return "redirect:/login"; // Перенаправление на страницу входа
    }
}