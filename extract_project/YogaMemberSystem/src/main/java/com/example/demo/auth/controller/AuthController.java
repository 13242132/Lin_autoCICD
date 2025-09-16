package com.example.demo.auth.controller;

import com.example.demo.auth.service.AuthService;
import com.example.demo.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            String phone = (String) request.get("phone");
            String password = (String) request.get("password");
            String confirmPassword = (String) request.get("confirmPassword");
            String code = (String) request.get("code");
            Boolean agreeTerms = (Boolean) request.get("agreeTerms");

            Map<String, Object> result = authService.register(phone, password, confirmPassword, code, agreeTerms);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            Map<String, String> error = Map.of(
                "error", "REGISTER_FAILED",
                "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        try {
            String phone = (String) request.get("phone");
            String password = (String) request.get("password");
            Boolean rememberMe = (Boolean) request.get("rememberMe");

            Map<String, Object> result = authService.login(phone, password, rememberMe);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = Map.of(
                "error", "LOGIN_FAILED",
                "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            String phone = JwtUtil.getUsernameFromToken(token);

            Map<String, Object> result = authService.getCurrentUser(phone);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}