package com.demoApp.otp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP generation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequestDto {
    
    @NotBlank(message = "Recipient is required")
    private String recipient;
    
    private Long userId;
    
    private String purpose;
} 