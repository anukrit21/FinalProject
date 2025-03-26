package com.demoApp.otp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP verification requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationDto {
    
    @NotBlank(message = "Recipient is required")
    private String recipient;
    
    @NotBlank(message = "OTP is required")
    private String otp;
    
    private Long userId;
} 