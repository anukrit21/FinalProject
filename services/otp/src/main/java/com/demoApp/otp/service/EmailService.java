package com.demoApp.otp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service for sending emails
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@demoapp.com}")
    private String fromEmail;
    
    @Value("${app.name:DemoApp}")
    private String appName;
    
    /**
     * Send a simple email with OTP
     * @param to recipient email
     * @param otp OTP code
     */
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(appName + " - Your Verification Code");
        message.setText("Your verification code is: " + otp + "\n\nThis code will expire in 5 minutes.");
        
        mailSender.send(message);
    }
    
    /**
     * Send HTML email with OTP using Thymeleaf template if available
     * @param to recipient email
     * @param otp OTP code
     * @throws MessagingException if message cannot be created
     */
    public void sendOtpHtmlEmail(String to, String otp) throws MessagingException {
        if (templateEngine == null) {
            // Fall back to simple email if template engine not configured
            sendOtpEmail(to, otp);
            return;
        }
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("appName", appName);
        
        String htmlContent = templateEngine.process("otp-template", context);
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(appName + " - Your Verification Code");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
} 