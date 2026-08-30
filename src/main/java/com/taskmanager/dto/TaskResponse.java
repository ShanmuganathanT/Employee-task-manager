package com.taskmanager.dto;

import com.taskmanager.model.Task;

import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String description;
    private String employeeUsername;
    private String employeeName;
    private String assignedBy;
    private String status;
    private LocalDateTime createdAt;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        this.employeeUsername = task.getAssignedTo().getUsername();
        this.employeeName = task.getAssignedTo().getName();
        this.assignedBy = task.getAssignedBy();
        this.status = task.getStatus().name();
        this.createdAt = task.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
