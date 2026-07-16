package com.example.agrilinkback.module.auth.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.common.security.HeaderRoleAuthenticationFilter;
import com.example.agrilinkback.common.security.UserRole;
import com.example.agrilinkback.module.auth.dto.AuthResponse;
import com.example.agrilinkback.module.auth.dto.LoginRequest;
import com.example.agrilinkback.module.auth.dto.RegisterRequest;
import com.example.agrilinkback.module.user.entity.User;
import com.example.agrilinkback.module.user.mapper.UserMapper;
import com.example.agrilinkback.module.user.service.UserService;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录和注册服务。
 *
 * <p>返回的 token 当前使用角色码承载，配合 HeaderRoleAuthenticationFilter 完成实训阶段鉴权。
 */
@Service
public class AuthService {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private static final Pattern REGULAR_ACCOUNT = Pattern.compile("^[^\\s@]{3,32}$");
    private static final Pattern NETEASE_ACCOUNT = Pattern.compile("^[^\\s@]+@163\\.com$", Pattern.CASE_INSENSITIVE);

    private final UserMapper userMapper;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    public AuthService(UserMapper userMapper, UserService userService,
                       EmailVerificationService emailVerificationService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userMapper.findByUserName(request.userName());
        if (user == null || !passwordMatches(request.password(), user.password())) {
            throw new BusinessException(401, "Invalid username or password");
        }

        UserRole role = resolveRole(user.role());
        if (request.role() != null && !request.role().isBlank()) {
            // 登录页选择的角色必须和数据库中的用户角色一致，避免跨角色进入模块。
            UserRole expectedRole = resolveRole(request.role());
            if (expectedRole != role) {
                throw new BusinessException(403, "User role does not match requested login role");
            }
        }
        return toResponse(user, role);
    }

    public AuthResponse register(RegisterRequest request) {
        String userName = request.userName().trim();
        if (NETEASE_ACCOUNT.matcher(userName).matches()) {
            if (request.verificationCode() == null || request.verificationCode().isBlank()) {
                throw new BusinessException("163 email registration requires a verification code");
            }
            userName = userName.toLowerCase(Locale.ROOT);
            emailVerificationService.verify(userName, request.verificationCode());
        } else if (!REGULAR_ACCOUNT.matcher(userName).matches()) {
            throw new BusinessException("Regular account must be 3-32 characters and cannot contain spaces or @");
        }

        User user = userService.createUser(request.toUserRequest(userName));
        return toResponse(user, resolveRole(user.role()));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        // 兼容测试数据中的明文密码，同时支持生产数据使用 BCrypt 哈希。
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
