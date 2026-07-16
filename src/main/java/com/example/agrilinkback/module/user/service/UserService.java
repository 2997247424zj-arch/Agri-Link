package com.example.agrilinkback.module.user.service;

import com.example.agrilinkback.common.security.UserRole;
import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.user.dto.PasswordRequest;
import com.example.agrilinkback.module.user.dto.UserRequest;
import com.example.agrilinkback.module.user.entity.User;
import com.example.agrilinkback.module.user.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户基础资料服务。
 *
 * <p>该服务只允许创建业务角色用户，系统管理员由初始化数据维护。
 */
@Service
public class UserService {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<User> listUsers() {
        return userMapper.findAll();
    }

    public User getUser(String userName) {
        User user = userMapper.findByUserName(userName);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        return user;
    }

    public User createUser(UserRequest request) {
        if (userMapper.findByUserName(request.userName()) != null) {
            throw new BusinessException(409, "User already exists");
        }
        // 新注册用户给定默认积分和信用分，后续可由业务流程扩展调整。
        User user = toUser(request, LocalDateTime.now(), 500, 5);
        userMapper.insert(user);
        return getUser(request.userName());
    }

    public User updateUser(String userName, UserRequest request) {
        User existing = getUser(userName);
        User user = new User(
                userName,
                existing.password(),
                request.nickName(),
                request.phone(),
                request.identityNum(),
                request.address(),
                existing.role(),
                existing.createTime(),
                LocalDateTime.now(),
                existing.integral(),
                existing.credit(),
                request.avatar(),
                request.realName(),
                existing.enabled()
        );
        userMapper.update(user);
        return getUser(userName);
    }

    public void deleteUser(String userName) {
        getUser(userName);
        userMapper.deleteByUserName(userName);
    }

    public User updatePassword(String userName, PasswordRequest request) {
        getUser(userName);
        userMapper.updatePassword(userName, BCRYPT.encode(request.password()));
        return getUser(userName);
    }

    private User toUser(UserRequest request, LocalDateTime now, Integer integral, Integer credit) {
        return new User(
                request.userName(),
                BCRYPT.encode(request.password()),
                request.nickName(),
                request.phone(),
                request.identityNum(),
                request.address(),
                requireBusinessRole(request.role()).code(),
                now,
                now,
                integral,
                credit,
                request.avatar(),
                request.realName(),
                true
        );
    }

    private UserRole requireBusinessRole(String role) {
        UserRole userRole = UserRole.fromCode(role)
                .orElseThrow(() -> new BusinessException("Role must be BUYER, FARMER, EXPERT or BANK"));
        if (!userRole.isBusinessRole()) {
            // 防止通过普通用户接口注册或更新为 SYSTEM_ADMIN。
            throw new BusinessException("Role must be BUYER, FARMER, EXPERT or BANK");
        }
        return userRole;
    }
}
