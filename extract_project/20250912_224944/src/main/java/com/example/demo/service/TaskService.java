package com.example.demo.service;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AuditLog;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.api.querydto.NotificationQueryDTO;
import com.example.demo.api.service.NotificationApiService;
import com.example.demo.request.TaskRequests;
import com.example.demo.response.TaskResponses;

@Service
public class TaskService {

    private final TaskRepository repository;

    @Autowired
    private NotificationApiService notificationApiService;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    // 创建任务
    public Task createTask(Task task) {
        task.setStatus("未开始");
        task.setCreatedAt(LocalDateTime.now());
        return repository.create(task);
    }

    // 获取任务详情
    public Optional<Task> getTaskById(Long id) {
        return repository.findById(id);
    }

    // 分页查询任务列表（支持按状态筛选）
    public TaskResponses.GetTasksResponse getTasks(TaskRequests.GetTasksByStatusRequest request) {
        Pageable pageable = PageRequest.of(0, 10); // 默认分页大小为10
        Page<Task> taskPage;

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            taskPage = repository.findByStatus(request.getStatus(), pageable);
        } else {
            taskPage = repository.findAll(pageable);
        }

        List<TaskResponses.TaskItem> items = taskPage.getContent().stream()
                .map(this::convertToTaskItem)
                .collect(Collectors.toList());

        TaskResponses.GetTasksResponse response = new TaskResponses.GetTasksResponse();
        response.setItems(items);
        response.setTotal(taskPage.getTotalElements());
        response.setPage(1);
        response.setSize(10);
        response.setTotalPages(taskPage.getTotalPages());
        return response;
    }

    // 转换Task实体为TaskItem（用于响应）
    private TaskResponses.GetTasksResponse.TaskItem convertToTaskItem(Task task) {
        TaskResponses.GetTasksResponse.TaskItem item = new TaskResponses.GetTasksResponse.TaskItem();
        item.setId(task.getId());
        item.setTitle(task.getTitle());
        item.setPriority(task.getPriority());
        item.setDueDate(task.getDueDate());
        item.setAssignee(task.getAssignee());
        item.setStatus(task.getStatus());
        item.setCreatedAt(task.getCreatedAt());
        return item;
    }

    // 更新任务状态
    public Task updateTaskStatus(Long taskId, String newStatus) {
        Optional<Task> taskOpt = repository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("任务不存在");
        }

        Task task = taskOpt.get();
        String oldStatus = task.getStatus();

        if ("已完成".equals(oldStatus) && !"已完成".equals(newStatus)) {
            throw new RuntimeException("已完成状态不可变更");
        }

        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());

        // 添加审计日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("STATUS_UPDATED");
        auditLog.setTargetType("Task");
        auditLog.setTargetId(taskId);
        auditLog.setDetails(String.format("从 '%s' 变更为 '%s'", oldStatus, newStatus));
        auditLog.setCreatedAt(LocalDateTime.now());

        // 发送通知
        NotificationQueryDTO queryDTO = new NotificationQueryDTO();
        queryDTO.setUserId(task.getAssignee());
        List<Notification> notifications = notificationApiService.queryByConditions(queryDTO);

        // 保存任务
        return repository.save(task);
    }

    // 获取任务历史记录（模拟）
    public TaskResponses.GetTaskHistoryResponse getTaskHistory(Long taskId) {
        TaskResponses.GetTaskHistoryResponse response = new TaskResponses.GetTaskHistoryResponse();
        List<TaskResponses.HistoryItem> historyItems = new ArrayList<>();

        // 模拟历史记录
        TaskResponses.HistoryItem item1 = new TaskResponses.HistoryItem();
        item1.setTimestamp("2025-04-05 10:00:00");
        item1.setAction("任务创建");
        item1.setFrom(null);
        item1.setTo("未开始");

        TaskResponses.HistoryItem item2 = new TaskResponses.HistoryItem();
        item2.setTimestamp("2025-04-05 11:30:00");
        item2.setAction("状态变更为“进行中”");
        item2.setFrom("未开始");
        item2.setTo("进行中");

        historyItems.add(item1);
        historyItems.add(item2);

        response.setHistory(historyItems);
        return response;
    }
}
