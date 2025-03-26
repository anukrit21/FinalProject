package com.demoApp.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private Long userId;
    private String username;
    private String token;
    private String role;
    private LocalDateTime expiresAt;
    private String status;
    private String message;
} 