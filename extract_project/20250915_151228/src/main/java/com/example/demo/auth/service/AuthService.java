package com.example.demo.auth.service;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;


import com.example.demo.entity.User;
import com.example.demo.auth.repository.AuthRepository;
import com.example.demo.auth.util.JwtUtil;
import com.example.demo.auth.response.AuthResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    // 用户名正则：字母开头，允许字母数字下划线，3-20字符
    private static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{2,19}$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    public AuthResponses.RegisterResponse register(
            String username,
            String email,
            String password) {

        // 校验用户名格式
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名格式不合法");
        }

        // 校验邮箱唯一性
        if (authRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已存在");
        }

        // 校验用户名唯一性
        if (authRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // 实际项目中应加密存储
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        authRepository.save(user);

        return new AuthResponses.RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    public AuthResponses.LoginResponse login(String usernameOrEmail, String password) {
        User user = null;

        // 判断是用户名还是邮箱登录
        if (usernameOrEmail.contains("@")) {
            user = authRepository.findByEmail(usernameOrEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名/邮箱或密码错误"));
        } else {
            user = authRepository.findByUsername(usernameOrEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名/邮箱或密码错误"));
        }

        // 密码校验（实际项目中使用 BCrypt 加密比较）
        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名/邮箱或密码错误");
        }

        // 生成 JWT Token
        String token = JwtUtil.generateToken(user.getId());

        AuthResponses.UserInfo userInfo = new AuthResponses.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());

        return new AuthResponses.LoginResponse(token, userInfo);
    }
}
