package com.example.demo.api.service;

import com.example.demo.entity.Comment;
import com.example.demo.api.querydto.CommentQueryDTO;
import java.util.List;
import java.util.Optional;

public interface CommentApiService {
    List<Comment> findAll();
    Optional<Comment> findById(Long id);
    Comment save(Comment entity);
    void deleteById(Long id);
    List<Comment> queryByConditions(CommentQueryDTO queryDTO);
}
