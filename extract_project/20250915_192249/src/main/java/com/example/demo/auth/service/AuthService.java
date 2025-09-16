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
import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public AuthResponses.RegisterResponse register(String username, String email, String password) {
        // 检查用户名是否已存在
        if (authRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "REGISTRATION_FAILED", 
                new RuntimeException("用户名已存在"));
        }

        // 检查邮箱是否已存在
        if (authRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "REGISTRATION_FAILED", 
                new RuntimeException("邮箱已存在"));
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // 实际项目中应加密
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
        User user = authRepository.findByUsername(usernameOrEmail)
            .or(() -> authRepository.findByEmail(usernameOrEmail))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS"));

        if (!Objects.equals(user.getPassword(), password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }

        String token = JwtUtil.generateToken(user.getId());
        return new AuthResponses.LoginResponse(token, user.getId(), user.getUsername());
    }
}
