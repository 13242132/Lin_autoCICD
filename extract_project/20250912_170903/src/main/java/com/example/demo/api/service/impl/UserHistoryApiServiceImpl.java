package com.example.demo.api.service.impl;

import com.example.demo.entity.UserHistory;
import com.example.demo.api.querydto.UserHistoryQueryDTO;
import com.example.demo.api.repository.UserHistoryApiRepository;
import com.example.demo.api.service.UserHistoryApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserHistoryApiServiceImpl implements UserHistoryApiService {

    @Autowired
    private UserHistoryApiRepository repository;

    @Override
    public List<UserHistory> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<UserHistory> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public UserHistory save(UserHistory entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<UserHistory> queryByConditions(UserHistoryQueryDTO queryDTO) {
        Specification<UserHistory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 使用反射获取QueryDTO的所有字段
            try {
                for (java.lang.reflect.Field field : queryDTO.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(queryDTO);
                    if (value != null) {
                        String fieldName = field.getName();
                        // 所有类型都使用等值查询
                        predicates.add(cb.equal(root.get(fieldName), value));
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("获取查询条件失败", e);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec);
    }
}
