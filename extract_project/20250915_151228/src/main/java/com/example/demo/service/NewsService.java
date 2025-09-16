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

import com.example.demo.entity.News;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Like;
import com.example.demo.entity.User;
import com.example.demo.repository.NewsRepository;
import com.example.demo.api.querydto.CommentQueryDTO;
import com.example.demo.api.querydto.LikeQueryDTO;
import com.example.demo.api.service.CommentApiService;
import com.example.demo.api.service.LikeApiService;
import com.example.demo.request.NewsRequests;
import com.example.demo.response.NewsResponses;
import com.example.demo.exception.BusinessException;

@Service
public class NewsService {

    private final NewsRepository repository;

    @Autowired
    private CommentApiService commentApiService;

    @Autowired
    private LikeApiService likeApiService;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    // 创建新闻
    public News createNews(News entity) {
        if (entity.getTitle() == null || entity.getTitle().isEmpty()) {
            throw new BusinessException("INVALID_DATA", "标题不能为空");
        }
        if (entity.getSource() == null || entity.getSource().isEmpty()) {
            throw new BusinessException("INVALID_DATA", "来源不能为空");
        }
        return repository.insert(entity);
    }

    // 分页查询新闻列表
    public NewsResponses.GetNewsListResponse getNewsList(NewsRequests.GetNewsListRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<News> newsPage = repository.findByCategoryAndSource(request.getCategory(), request.getSource(), pageable);

        NewsResponses.GetNewsListResponse response = new NewsResponses.GetNewsListResponse();
        response.setNews(newsPage.getContent().stream()
                .map(news -> {
                    NewsResponses.NewsItem item = new NewsResponses.NewsItem();
                    item.setId(news.getId());
                    item.setTitle(news.getTitle());
                    item.setSource(news.getSource());
                    item.setPublishedAt(news.getPublishedAt());
                    item.setSummary(news.getSummary());
                    return item;
                })
                .collect(Collectors.toList()));
        response.setTotal(newsPage.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotalPages(newsPage.getTotalPages());
        return response;
    }

    // 获取新闻详情及关联信息
    public NewsResponses.GetNewsDetailResponse getNewsDetail(Long id, Long currentUserId) {
        Optional<News> newsOpt = repository.findById(id);
        if (!newsOpt.isPresent()) {
            throw new BusinessException("NEWS_NOT_FOUND", "新闻不存在");
        }

        News news = newsOpt.get();

        // 查询评论
        CommentQueryDTO commentQueryDTO = new CommentQueryDTO();
        commentQueryDTO.setNewsId(id);
        List<Comment> comments = commentApiService.queryByConditions(commentQueryDTO);

        // 查询点赞数和是否点赞
        LikeQueryDTO likeQueryDTO = new LikeQueryDTO();
        likeQueryDTO.setNewsId(id);
        List<Like> likes = likeApiService.queryByConditions(likeQueryDTO);
        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = likes.stream().anyMatch(like -> like.getUserId().equals(currentUserId));
        }

        NewsResponses.GetNewsDetailResponse response = new NewsResponses.GetNewsDetailResponse();
        response.setId(news.getId());
        response.setTitle(news.getTitle());
        response.setSource(news.getSource());
        response.setPublishedAt(news.getPublishedAt());
        response.setSummary(news.getSummary());
        response.setUrl(news.getUrl());
        response.setImageUrl(news.getImageUrl());
        response.setComments(comments.stream().map(comment -> {
            NewsResponses.CommentItem item = new NewsResponses.CommentItem();
            item.setId(comment.getId());
            item.setUserId(comment.getUserId());
            item.setContent(comment.getContent());
            item.setCreatedAt(comment.getCreatedAt());
            return item;
        }).collect(Collectors.toList()));
        response.setLikesCount(likes.size());
        response.setLiked(isLiked);
        return response;
    }
}
