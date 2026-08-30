package com.taskmanager.service;

import com.taskmanager.dto.NewTaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.exception.ApiException;
import com.taskmanager.model.Role;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> allTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TaskResponse::new)
                .toList();
    }

    public List<TaskResponse> tasksForEmployee(String username) {
        return taskRepository.findByAssignedTo_UsernameOrderByCreatedAtDesc(username).stream()
                .map(TaskResponse::new)
                .toList();
    }

    public TaskResponse assignTask(NewTaskRequest request) {
        User employee = userRepository.findByUsername(request.getEmployeeUsername())
                .orElseThrow(() -> new ApiException("No such employee: " + request.getEmployeeUsername()));

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new ApiException(employee.getUsername() + " is not an employee");
        }

        Task task = new Task(request.getDescription(), employee, request.getAssignedBy());
        return new TaskResponse(taskRepository.save(task));
    }

    public TaskResponse updateStatus(Long taskId, String statusStr) {
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Unknown status: " + statusStr);
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException("Task not found: " + taskId));

        task.setStatus(status);
        return new TaskResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ApiException("Task not found: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }
}
