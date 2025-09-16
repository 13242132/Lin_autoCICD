package com.example.demo.service;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.User;
import com.example.demo.entity.News;
import com.example.demo.repository.CommentRepository;
import com.example.demo.api.querydto.CommentQueryDTO;
import com.example.demo.api.service.UserApiService;
import com.example.demo.api.service.NewsApiService;
import com.example.demo.request.CommentRequests;
import com.example.demo.response.CommentResponses;
import com.example.demo.exception.BusinessException;

@Service
public class CommentService {

    private final CommentRepository repository;

    // 注入外部模块的Service
    @Autowired
    private UserApiService userApiService;

    @Autowired
    private NewsApiService newsApiService;

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    // 调用数据库层：插入评论
    public Comment createComment(Comment entity) {
        if (entity.getContent() == null || entity.getContent().trim().isEmpty()) {
            throw new BusinessException("COMMENT_EMPTY", "评论内容不能为空");
        }
        entity.setCreatedAt(LocalDateTime.now());
        return repository.insert(entity);
    }

    // 根据ID查询评论
    public Comment getCommentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("COMMENT_NOT_FOUND", "评论不存在"));
    }

    // 根据新闻ID查询评论列表
    public List<Comment> getCommentsByNewsId(Long newsId) {
        return repository.findByNewsId(newsId);
    }

    // 根据用户ID查询评论列表
    public List<Comment> getCommentsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    // 分页查询新闻下的评论
    public CommentResponses.GetCommentsByNewsIdResponse getCommentsByNewsIdWithPagination(
            Long newsId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> commentPage = repository.findByNewsIdWithPagination(newsId, pageable);

        CommentResponses.GetCommentsByNewsIdResponse response = new CommentResponses.GetCommentsByNewsIdResponse();
        response.setComments(commentPage.getContent().stream()
                .map(comment -> {
                    CommentResponses.CommentItem item = new CommentResponses.CommentItem();
                    item.setId(comment.getId());
                    item.setNewsId(comment.getNewsId());
                    item.setUserId(comment.getUserId());
                    item.setContent(comment.getContent());
                    item.setCreatedAt(comment.getCreatedAt());
                    return item;
                })
                .collect(Collectors.toList()));
        response.setTotal(commentPage.getTotalElements());
        response.setPage(page);
        response.setSize(size);
        response.setTotalPages(commentPage.getTotalPages());
        return response;
    }

    // 查询用户对某新闻的评论
    public Comment getUserCommentOnNews(Long newsId, Long userId) {
        return repository.findByNewsIdAndUserId(newsId, userId)
                .orElseThrow(() -> new BusinessException("COMMENT_NOT_FOUND", "未找到该用户的评论"));
    }

    // 删除评论
    public void deleteComment(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("COMMENT_NOT_FOUND", "评论不存在");
        }
        repository.deleteById(id);
    }
}
