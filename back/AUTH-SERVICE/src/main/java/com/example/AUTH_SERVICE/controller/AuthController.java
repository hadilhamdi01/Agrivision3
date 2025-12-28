package com.example.AUTH_SERVICE.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint OK";
    }

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "Secure endpoint - Token valid";
    }
}