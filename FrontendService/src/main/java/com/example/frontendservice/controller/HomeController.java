package com.example.frontendservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/store")
    public String home(Model model) {
        model.addAttribute("title", "Добро пожаловать в наш интернет-магазин");
        model.addAttribute("welcomeMessage", "Добро пожаловать в наш интернет-магазин!");
        model.addAttribute("description", "Ваш универсальный магазин для невероятных товаров и выгодных предложений.");
        return "store";
    }
}