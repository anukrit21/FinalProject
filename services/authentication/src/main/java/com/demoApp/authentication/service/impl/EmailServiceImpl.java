package com.demoApp.authentication.service.impl;

import com.demoApp.authentication.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final Optional<JavaMailSender> mailSender;
    
    @Value("${spring.mail.username:noreply@demoapp.com}")
    private String fromEmail;
    
    @Value("${app.url:https://demoapp.com}")
    private String appUrl;
    
    @Autowired
    public EmailServiceImpl(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        if (!mailSender.isPresent()) {
            log.info("Mail sender not configured. Would have sent password reset email to: {}", to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("DemoApp - Password Reset Request");
            message.setText("Hello,\n\n" +
                    "You have requested to reset your password. Please click the link below to set a new password:\n\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 24 hours. If you did not request a password reset, please ignore this email.\n\n" +
                    "Regards,\nDemoApp Team");
            
            mailSender.get().send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
        }
    }

    @Override
    public void sendAccountLockedEmail(String to, String username) {
        if (!mailSender.isPresent()) {
            log.info("Mail sender not configured. Would have sent account locked email to: {}", to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("DemoApp - Account Locked");
            message.setText("Hello " + username + ",\n\n" +
                    "Your account has been temporarily locked due to multiple failed login attempts. " +
                    "This is a security measure to protect your account.\n\n" +
                    "You can reset your password by clicking here: " + appUrl + "/reset-password\n\n" +
                    "If you did not attempt to log in to your account, please consider changing your password immediately.\n\n" +
                    "Regards,\nDemoApp Team");
            
            mailSender.get().send(message);
            log.info("Account locked email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send account locked email to: {}", to, e);
        }
    }

    @Override
    public void sendMfaSetupEmail(String to, String setupLink) {
        if (!mailSender.isPresent()) {
            log.info("Mail sender not configured. Would have sent MFA setup email to: {}", to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("DemoApp - Multi-Factor Authentication Setup");
            message.setText("Hello,\n\n" +
                    "You have enabled Multi-Factor Authentication (MFA) for your DemoApp account. " +
                    "This adds an extra layer of security to your account.\n\n" +
                    "Please complete the setup by clicking the link below:\n\n" +
                    setupLink + "\n\n" +
                    "If you did not request this change, please contact our support team immediately.\n\n" +
                    "Regards,\nDemoApp Team");
            
            mailSender.get().send(message);
            log.info("MFA setup email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send MFA setup email to: {}", to, e);
        }
    }

    @Override
    public void sendOAuthRegistrationEmail(String to, String username) {
        if (!mailSender.isPresent()) {
            log.info("Mail sender not configured. Would have sent OAuth registration email to: {}", to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to DemoApp");
            message.setText("Hello " + username + ",\n\n" +
                    "Thank you for creating an account with DemoApp using your social login. " +
                    "Your account has been successfully created.\n\n" +
                    "You can access your account here: " + appUrl + "/login\n\n" +
                    "If you have any questions or need assistance, please don't hesitate to contact our support team.\n\n" +
                    "Regards,\nDemoApp Team");
            
            mailSender.get().send(message);
            log.info("OAuth registration email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OAuth registration email to: {}", to, e);
        }
    }
} 