package com.example.agrilinkback.module.user.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.user.dto.PasswordRequest;
import com.example.agrilinkback.module.user.dto.UserRequest;
import com.example.agrilinkback.module.user.entity.User;
import com.example.agrilinkback.module.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }

    @GetMapping("/{userName}")
    public ApiResponse<User> getUser(@PathVariable String userName) {
        return ApiResponse.success(userService.getUser(userName));
    }

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @PutMapping("/{userName}")
    public ApiResponse<User> updateUser(@PathVariable String userName, @Valid @RequestBody UserRequest request) {
        return ApiResponse.success(userService.updateUser(userName, request));
    }

    @DeleteMapping("/{userName}")
    public ApiResponse<Void> deleteUser(@PathVariable String userName) {
        userService.deleteUser(userName);
        return ApiResponse.ok();
    }

    @PatchMapping("/{userName}/password")
    public ApiResponse<User> updatePassword(
            @PathVariable String userName,
            @Valid @RequestBody PasswordRequest request
    ) {
        return ApiResponse.success(userService.updatePassword(userName, request));
    }
}
