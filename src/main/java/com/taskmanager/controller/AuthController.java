package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        UserResponse user = authService.login(request.getUsername(), request.getPassword(), request.getRole());
        return ApiResponse.ok("Login successful", user);
    }

    @GetMapping("/employees")
    public ApiResponse<List<UserResponse>> listEmployees() {
        return ApiResponse.ok(authService.listEmployees());
    }

    @PostMapping("/employees")
    public ApiResponse<UserResponse> createEmployee(@Valid @RequestBody NewEmployeeRequest request) {
        UserResponse created = authService.createEmployee(request);
        return ApiResponse.ok("Employee created", created);
    }
}
