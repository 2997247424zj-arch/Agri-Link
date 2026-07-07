package com.example.agrilinkback.module.auth.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.auth.dto.AuthResponse;
import com.example.agrilinkback.module.auth.dto.LoginRequest;
import com.example.agrilinkback.module.auth.service.AuthService;
import com.example.agrilinkback.module.user.dto.UserRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody UserRequest request) {
        return ApiResponse.success(authService.register(request));
    }
}
