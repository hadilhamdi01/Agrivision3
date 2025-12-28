package com.example.AUTH_SERVICE.controller;

import com.example.AUTH_SERVICE.dto.RegisterRequest;
import com.example.AUTH_SERVICE.services.KeycloakUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    private final KeycloakUserService keycloakUserService;

    public AuthController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    //  EXISTANT – PUBLIC
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint OK";
    }

    //  EXISTANT – PROTÉGÉ PAR KEYCLOAK
    @GetMapping("/secure")
    public String secureEndpoint() {
        return "Secure endpoint - Token valid";
    }

    //  REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        keycloakUserService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }
}
