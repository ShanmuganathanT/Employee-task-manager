package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskStatusRequest {

    @NotBlank
    private String status; // PENDING, IN_PROGRESS, COMPLETED

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
