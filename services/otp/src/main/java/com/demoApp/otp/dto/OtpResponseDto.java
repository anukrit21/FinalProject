package com.demoApp.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponseDto {
    
    private String recipient;
    
    private String message;
    
    private String maskedOtp;
    
    private boolean verified;
    
    private Long expiresInMinutes;
} 