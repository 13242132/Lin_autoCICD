package com.example.demo.service;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Topic;
import com.example.demo.repository.TopicRepository;
import com.example.demo.response.TopicResponses;

@Service
public class TopicService {

    private final TopicRepository repository;

    public TopicService(TopicRepository repository) {
        this.repository = repository;
    }

    // 查询所有主题
    public List<TopicResponses.TopicItem> getAllTopics() {
        List<Topic> topics = repository.findAllTopics();
        return topics.stream()
                .map(topic -> {
                    TopicResponses.TopicItem item = new TopicResponses.TopicItem();
                    item.setId(topic.getId());
                    item.setName(topic.getName());
                    item.setDescription(topic.getDescription());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
