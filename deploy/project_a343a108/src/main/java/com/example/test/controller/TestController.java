package com.example.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String home() {
        return "Hello from Test Deployment Project running on port 8090!";
    }
    
    @GetMapping("/api/status")
    public String status() {
        return "{\"status\":\"running\",\"port\":8090,\"message\":\"Test deployment project is working correctly!\"}";
    }
}