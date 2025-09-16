package com.example.demo.controller;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.request.UserRequests;
import com.example.demo.response.UserResponses;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // 获取用户列表
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String role,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        try {
            UserRequests.GetUsersRequest request = new UserRequests.GetUsersRequest();
            request.setRole(role);
            UserResponses.GetUsersResponse response = service.listUsers(request, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserResponses.DeleteUserResponse("INTERNAL_ERROR", "数据查询失败"));
        }
    }

    // 删除用户
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        boolean deleted = service.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new UserResponses.DeleteUserResponse("USER_NOT_FOUND", "用户不存在"));
        }
    }
}
