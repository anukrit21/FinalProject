package com.demoApp.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.demoApp.user.model.User;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${app.email.verification.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${spring.mail.username:noreply@demoapp.com}")
    private String fromEmail;
    
    public void sendVerificationEmail(User user) {
        try {
            String token = UUID.randomUUID().toString();
            // In a real app, save this token along with the user ID and an expiration timestamp
            
            String verificationUrl = baseUrl + "/api/v1/auth/verify?token=" + token;
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Verify Your Email Address");
            message.setText("Hello " + user.getName() + ",\n\n" +
                    "Please verify your email address by clicking the link below:\n" +
                    verificationUrl + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you did not sign up for an account, please ignore this email.\n\n" +
                    "Best Regards,\nDemoApp Team");
            
            mailSender.send(message);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }
    
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Reset Your Password");
            message.setText("Hello " + user.getName() + ",\n\n" +
                    "You have requested to reset your password. Please click the link below to reset it:\n" +
                    resetUrl + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best Regards,\nDemoApp Team");
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
        }
    }
}
