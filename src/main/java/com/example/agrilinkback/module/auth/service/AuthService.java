package com.example.agrilinkback.module.auth.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.common.security.HeaderRoleAuthenticationFilter;
import com.example.agrilinkback.common.security.UserRole;
import com.example.agrilinkback.module.auth.dto.AuthResponse;
import com.example.agrilinkback.module.auth.dto.LoginRequest;
import com.example.agrilinkback.module.user.dto.UserRequest;
import com.example.agrilinkback.module.user.entity.User;
import com.example.agrilinkback.module.user.mapper.UserMapper;
import com.example.agrilinkback.module.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private final UserMapper userMapper;
    private final UserService userService;

    public AuthService(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userMapper.findByUserName(request.userName());
        if (user == null || !passwordMatches(request.password(), user.password())) {
            throw new BusinessException(401, "Invalid username or password");
        }

        UserRole role = resolveRole(user.role());
        if (request.role() != null && !request.role().isBlank()) {
            UserRole expectedRole = resolveRole(request.role());
            if (expectedRole != role) {
                throw new BusinessException(403, "User role does not match requested login role");
            }
        }
        return toResponse(user, role);
    }

    public AuthResponse register(UserRequest request) {
        User user = userService.createUser(request);
        return toResponse(user, resolveRole(user.role()));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$")) {
            try {
                return BCRYPT.matches(rawPassword, storedPassword);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return storedPassword.equals(rawPassword);
    }

    private UserRole resolveRole(String role) {
        return UserRole.fromCode(role)
                .orElseThrow(() -> new BusinessException(401, "User role is invalid"));
    }

    private AuthResponse toResponse(User user, UserRole role) {
        return new AuthResponse(
                user.userName(),
                user.nickName(),
                user.realName(),
                role.code(),
                role.label(),
                role.code(),
                HeaderRoleAuthenticationFilter.ROLE_HEADER
        );
    }
}
