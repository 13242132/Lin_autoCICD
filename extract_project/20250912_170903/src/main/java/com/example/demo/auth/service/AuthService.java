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

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public AuthResponses.RegisterResponse register(String username, String email, String password) {
        if (authRepository.findByUsernameOrEmail(username, email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名或邮箱已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // 注意：实际项目中应加密存储密码
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
        User user = authRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));

        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        user.setLastLoginAt(LocalDateTime.now());
        authRepository.save(user);

        String token = JwtUtil.generateToken(user.getId());
        long expirationTime = System.currentTimeMillis() + 8 * 60 * 60 * 1000; // 8小时后过期

        return new AuthResponses.LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(expirationTime), java.time.ZoneId.systemDefault())
        );
    }
}
