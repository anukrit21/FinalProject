package com.demoApp.authentication.service;

public interface EmailService {
    
    void sendPasswordResetEmail(String to, String resetLink);
    
    void sendAccountLockedEmail(String to, String username);
    
    void sendMfaSetupEmail(String to, String setupLink);
    
    void sendOAuthRegistrationEmail(String to, String username);
} 