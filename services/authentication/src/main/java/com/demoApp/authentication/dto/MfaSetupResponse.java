package com.demoApp.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupResponse {
    
    private String secretKey;
    private String qrCodeUrl;
    private boolean mfaEnabled;
    private String message;
} 