package com.example.frontendservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(@RequestParam(required = false) String token) {
        // Логика удаления токена или выполнения необходимых действий
        if (token != null) {
            System.out.println();

        }
        return "redirect:/auth/login";
    }
}
