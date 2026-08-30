package com.taskmanager.service;

import com.taskmanager.dto.NewEmployeeRequest;
import com.taskmanager.dto.UserResponse;
import com.taskmanager.exception.ApiException;
import com.taskmanager.model.Role;
import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse login(String username, String rawPassword, String roleStr) {
        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Unknown role: " + roleStr);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Invalid username or password"));

        if (user.getRole() != role) {
            throw new ApiException("Invalid credentials for the selected portal");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ApiException("Invalid username or password");
        }

        return new UserResponse(user);
    }

    public List<UserResponse> listEmployees() {
        return userRepository.findByRole(Role.EMPLOYEE).stream()
                .map(UserResponse::new)
                .toList();
    }

    public UserResponse createEmployee(NewEmployeeRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username already taken");
        }
        User user = new User(
                request.getName(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.EMPLOYEE
        );
        return new UserResponse(userRepository.save(user));
    }
}
