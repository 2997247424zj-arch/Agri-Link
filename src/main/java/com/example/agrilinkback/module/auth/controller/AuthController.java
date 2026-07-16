package com.example.agrilinkback.module.auth.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.auth.dto.AuthResponse;
import com.example.agrilinkback.module.auth.dto.EmailCodeRequest;
import com.example.agrilinkback.module.auth.dto.LoginRequest;
import com.example.agrilinkback.module.auth.dto.RegisterRequest;
import com.example.agrilinkback.module.auth.service.AuthService;
import com.example.agrilinkback.module.auth.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证入口，负责登录和注册。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService, EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/email-code")
    public ApiResponse<Void> sendEmailCode(@Valid @RequestBody EmailCodeRequest request) {
        emailVerificationService.sendCode(request.email());
        return ApiResponse.ok();
    }
}
