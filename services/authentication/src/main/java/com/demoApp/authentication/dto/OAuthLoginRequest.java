package com.demoApp.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginRequest {
    
    @NotBlank(message = "Provider is required")
    private String provider; // e.g., google, facebook, github
    
    @NotBlank(message = "Access token is required")
    private String accessToken;
} 