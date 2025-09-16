package com.example.demo.service;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.UserRequests;
import com.example.demo.response.UserResponses;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // 查询所有用户
    public List<User> getAllUsers() {
        return repository.findAllUsers();
    }

    // 根据角色查询用户
    public List<User> getUsersByRole(String role) {
        return repository.findByRole(role);
    }

    // 分页查询用户（按角色）
    public UserResponses.GetUsersResponse listUsers(UserRequests.GetUsersRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage;

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            userPage = repository.findByRole(request.getRole(), pageable);
        } else {
            userPage = repository.findAll(pageable);
        }

        List<UserResponses.GetUsersResponse.UserItem> items = userPage.getContent().stream()
                .map(user -> {
                    UserResponses.GetUsersResponse.UserItem item = new UserResponses.GetUsersResponse.UserItem();
                    item.setId(user.getId());
                    item.setUsername(user.getUsername());
                    item.setRole(user.getRole());
                    item.setCreatedAt(user.getCreatedAt().toString());
                    return item;
                })
                .collect(Collectors.toList());

        UserResponses.GetUsersResponse response = new UserResponses.GetUsersResponse();
        response.setUsers(items);
        response.setTotal(userPage.getTotalElements());
        response.setPage(page);
        response.setSize(size);
        response.setTotalPages(userPage.getTotalPages());
        return response;
    }

    // 删除用户
    public boolean deleteUser(Long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            repository.deleteById(userId);
            return true;
        }
        return false;
    }
}
