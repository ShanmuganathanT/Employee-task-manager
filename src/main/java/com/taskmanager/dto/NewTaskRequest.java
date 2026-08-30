package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class NewTaskRequest {

    @NotBlank
    private String employeeUsername;

    @NotBlank
    private String description;

    @NotBlank
    private String assignedBy;

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
}
