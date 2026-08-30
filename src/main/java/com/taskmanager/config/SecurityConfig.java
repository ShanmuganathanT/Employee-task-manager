package com.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This app does its own lightweight, request-based auth via /api/auth/login
 * (no server-side session/JWT), so Spring Security here is only wired up to:
 *   1) supply a PasswordEncoder bean (BCrypt) for hashing stored passwords, and
 *   2) get out of the way so the REST API and static frontend are reachable.
 *
 * NOTE: This is intentionally simple for a learning/demo project. For a real
 * production deployment you'd want proper session/JWT-based authentication
 * and per-endpoint authorization here.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable());
        return http.build();
    }
}
