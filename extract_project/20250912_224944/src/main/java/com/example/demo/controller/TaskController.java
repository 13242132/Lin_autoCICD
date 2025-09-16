package com.example.demo.controller;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Task;
import com.example.demo.request.TaskRequests;
import com.example.demo.response.TaskResponses;
import com.example.demo.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // 创建任务
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequests.CreateTaskRequest request) {
        try {
            Task task = new Task();
            task.setTitle(request.getTitle());
            task.setDescription(request.getDescription());
            task.setPriority(request.getPriority());
            task.setDueDate(request.getDueDate());
            task.setAssignee(request.getAssignee());

            Task createdTask = service.createTask(task);

            TaskResponses.CreateTaskResponse response = new TaskResponses.CreateTaskResponse();
            response.setId(createdTask.getId());
            response.setTitle(createdTask.getTitle());
            response.setDescription(createdTask.getDescription());
            response.setPriority(createdTask.getPriority());
            response.setDueDate(createdTask.getDueDate());
            response.setAssignee(createdTask.getAssignee());
            response.setStatus(createdTask.getStatus());
            response.setCreatedAt(createdTask.getCreatedAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            TaskResponses.ErrorResponse error = new TaskResponses.ErrorResponse();
            error.setError("TASK_CREATION_FAILED");
            error.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 获取任务列表
    @GetMapping
    public ResponseEntity<?> getTasks(TaskRequests.GetTasksByStatusRequest request) {
        try {
            TaskResponses.GetTasksResponse response = service.getTasks(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TaskResponses.ErrorResponse error = new TaskResponses.ErrorResponse();
            error.setError("INTERNAL_ERROR");
            error.setMessage("服务器错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 更新任务状态
    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody TaskRequests.UpdateTaskStatusRequest request) {
        try {
            Task updatedTask = service.updateTaskStatus(taskId, request.getStatus());

            TaskResponses.UpdateTaskStatusResponse response = new TaskResponses.UpdateTaskStatusResponse();
            response.setId(updatedTask.getId());
            response.setTitle(updatedTask.getTitle());
            response.setStatus(updatedTask.getStatus());
            response.setUpdatedAt(updatedTask.getUpdatedAt());

            TaskResponses.GetTaskHistoryResponse historyResponse = service.getTaskHistory(taskId);
            response.setHistory(historyResponse.getHistory());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            TaskResponses.ErrorResponse error = new TaskResponses.ErrorResponse();
            error.setError("INVALID_STATUS_TRANSITION");
            error.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            TaskResponses.ErrorResponse error = new TaskResponses.ErrorResponse();
            error.setError("INTERNAL_ERROR");
            error.setMessage("服务器错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
