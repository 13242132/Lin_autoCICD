package com.example.demo.controller;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.request.CommentRequests;
import com.example.demo.response.CommentResponses;
import com.example.demo.exception.BusinessException;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    // 创建评论
    @PostMapping
    public ResponseEntity<CommentResponses.CreateCommentResponse> create(
            @RequestBody CommentRequests.CreateCommentRequest request) {
        Comment comment = new Comment();
        comment.setNewsId(request.getNewsId());
        comment.setUserId(request.getUserId());
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = service.createComment(comment);

        CommentResponses.CreateCommentResponse response = new CommentResponses.CreateCommentResponse();
        response.setId(saved.getId());
        response.setNewsId(saved.getNewsId());
        response.setUserId(saved.getUserId());
        response.setContent(saved.getContent());
        response.setCreatedAt(saved.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 根据ID获取评论
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponses.GetCommentByIdResponse> getById(@PathVariable Long id) {
        Comment comment = service.getCommentById(id);

        CommentResponses.GetCommentByIdResponse response = new CommentResponses.GetCommentByIdResponse();
        response.setId(comment.getId());
        response.setNewsId(comment.getNewsId());
        response.setUserId(comment.getUserId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    // 根据新闻ID获取评论列表
    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<Comment>> getByNewsId(@PathVariable Long newsId) {
        List<Comment> comments = service.getCommentsByNewsId(newsId);
        return ResponseEntity.ok(comments);
    }

    // 根据用户ID获取评论列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getByUserId(@PathVariable Long userId) {
        List<Comment> comments = service.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }

    // 分页查询新闻下的评论
    @GetMapping("/news/{newsId}/page")
    public CommentResponses.GetCommentsByNewsIdResponse getCommentsByNewsIdWithPagination(
            @PathVariable Long newsId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.getCommentsByNewsIdWithPagination(newsId, page, size);
    }

    // 查询用户对某新闻的评论
    @GetMapping("/news/{newsId}/user/{userId}")
    public ResponseEntity<CommentResponses.GetUserCommentOnNewsResponse> getUserCommentOnNews(
            @PathVariable Long newsId, @PathVariable Long userId) {
        Comment comment = service.getUserCommentOnNews(newsId, userId);

        CommentResponses.GetUserCommentOnNewsResponse response = new CommentResponses.GetUserCommentOnNewsResponse();
        response.setId(comment.getId());
        response.setNewsId(comment.getNewsId());
        response.setUserId(comment.getUserId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    // 删除评论
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
