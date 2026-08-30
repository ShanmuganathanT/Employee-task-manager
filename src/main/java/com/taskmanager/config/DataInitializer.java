package com.taskmanager.config;

import com.taskmanager.model.Role;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(new User("System Admin", "admin", passwordEncoder.encode("1234"), Role.ADMIN));
        User employee = userRepository.save(new User("Jane Doe", "employee", passwordEncoder.encode("abcd"), Role.EMPLOYEE));
        User employee2 = userRepository.save(new User("Mark Lee", "mark", passwordEncoder.encode("abcd"), Role.EMPLOYEE));

        taskRepository.save(new Task("Update the weekly sales report for Q3.", employee, admin.getName()));
        taskRepository.save(new Task("Prepare onboarding docs for the new hire.", employee2, admin.getName()));

        System.out.println("Seed data loaded. Try admin/1234 or employee/abcd.");
    }
}
