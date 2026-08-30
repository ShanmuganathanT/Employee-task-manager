package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ApiResponse<List<TaskResponse>> allTasks() {
        return ApiResponse.ok(taskService.allTasks());
    }

    @GetMapping("/employee/{username}")
    public ApiResponse<List<TaskResponse>> tasksForEmployee(@PathVariable String username) {
        return ApiResponse.ok(taskService.tasksForEmployee(username));
    }

    @PostMapping
    public ApiResponse<TaskResponse> assignTask(@Valid @RequestBody NewTaskRequest request) {
        return ApiResponse.ok("Task assigned", taskService.assignTask(request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<TaskResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusRequest request) {
        return ApiResponse.ok("Status updated", taskService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ApiResponse.ok("Task deleted", null);
    }
}
