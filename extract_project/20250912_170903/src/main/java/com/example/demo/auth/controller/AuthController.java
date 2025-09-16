package com.example.demo.auth.controller;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.auth.repository.AuthRepository;
import com.example.demo.auth.util.JwtUtil;
import com.example.demo.entity.User;

import com.example.demo.auth.service.AuthService;
import com.example.demo.auth.request.AuthRequests;
import com.example.demo.auth.response.AuthResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponses.RegisterResponse> register(@RequestBody  AuthRequests.RegisterRequest request) {
        AuthResponses.RegisterResponse response = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponses.LoginResponse> login(@RequestBody  AuthRequests.LoginRequest request) {
        AuthResponses.LoginResponse response = authService.login(
                request.getUsernameOrEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(response);
    }
}
