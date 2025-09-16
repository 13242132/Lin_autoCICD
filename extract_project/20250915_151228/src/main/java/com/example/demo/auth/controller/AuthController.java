package com.example.demo.auth.controller;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
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

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponses.RegisterResponse> register(@RequestBody @Valid AuthRequests.RegisterRequest registerRequest) {
        AuthResponses.RegisterResponse response = authService.register(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponses.LoginResponse> login(@RequestBody @Valid AuthRequests.LoginRequest loginRequest) {
        AuthResponses.LoginResponse response = authService.login(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(response);
    }
}
