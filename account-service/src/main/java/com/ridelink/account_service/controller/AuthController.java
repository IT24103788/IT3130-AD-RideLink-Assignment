package com.ridelink.account_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final com.ridelink.account_service.service.AuthService authService;

    public AuthController(com.ridelink.account_service.service.AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@jakarta.validation.Valid @RequestBody com.ridelink.account_service.dto.RegisterRequest request) {
        String result = authService.registerUser(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<com.ridelink.account_service.dto.AuthResponse> login(@jakarta.validation.Valid @RequestBody com.ridelink.account_service.dto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}