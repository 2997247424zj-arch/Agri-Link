package com.example.agrilinkback.system.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.common.security.UserRole;
import com.example.agrilinkback.system.dto.HealthStatus;
import com.example.agrilinkback.system.dto.RoleInfo;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统公共接口，提供健康检查和前端角色字典。
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final String applicationName;

    public SystemController(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    @GetMapping("/health")
    public ApiResponse<HealthStatus> health() {
        return ApiResponse.success(new HealthStatus(applicationName, "UP", Instant.now()));
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleInfo>> roles() {
        List<RoleInfo> roles = Arrays.stream(UserRole.values())
                .map(role -> new RoleInfo(role.code(), role.label()))
                .toList();
        return ApiResponse.success(roles);
    }
}
