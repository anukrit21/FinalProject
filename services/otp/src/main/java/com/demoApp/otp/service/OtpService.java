package com.demoApp.otp.service;

import com.demoApp.otp.config.TwilioConfig;
import com.demoApp.otp.entity.OtpEntity;
import com.demoApp.otp.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class OtpService {
    private static final int EXPIRE_MINUTES = 5;
    private static final int OTP_LENGTH = 6;
    
    @Autowired
    private TwilioConfig twilioConfig;
    
    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private EmailService emailService;

    // Simple in-memory cache for OTPs
    private final Map<String, String> otpCache = new ConcurrentHashMap<>();

    /**
     * Generate and save a new OTP for email verification.
     */
    public OtpEntity generateEmailOtp(String email, Long userId) {
        String otp = generateRandomOtp();
        
        OtpEntity otpEntity = OtpEntity.builder()
                .recipient(email)
                .recipientType(OtpEntity.RecipientType.EMAIL)
                .otp(otp)
                .purpose(OtpEntity.OtpPurpose.EMAIL_VERIFICATION)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES))
                .build();
        
        otpRepository.save(otpEntity);
        otpCache.put(email, otp); // cache OTP
        
        // Send email with OTP
        emailService.sendOtpEmail(email, otp);
        
        return otpEntity;
    }
    
    /**
     * Generate and save a new OTP for phone verification.
     */
    public OtpEntity generatePhoneOtp(String phoneNumber, Long userId) {
        String otp = generateRandomOtp();
        
        OtpEntity otpEntity = OtpEntity.builder()
                .recipient(phoneNumber)
                .recipientType(OtpEntity.RecipientType.PHONE)
                .otp(otp)
                .purpose(OtpEntity.OtpPurpose.PHONE_VERIFICATION)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES))
                .build();
        
        otpRepository.save(otpEntity);
        otpCache.put(phoneNumber, otp); // cache OTP
        
        // Send SMS with OTP
        sendSmsOtp(phoneNumber, otp);
        
        return otpEntity;
    }
    
    /**
     * Generate OTP for any purpose.
     */
    public OtpEntity generateOtp(String recipient, OtpEntity.RecipientType recipientType, 
                                   OtpEntity.OtpPurpose purpose, Long userId) {
        String otp = generateRandomOtp();
        
        OtpEntity otpEntity = OtpEntity.builder()
                .recipient(recipient)
                .recipientType(recipientType)
                .otp(otp)
                .purpose(purpose)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES))
                .build();
        
        otpRepository.save(otpEntity);
        otpCache.put(recipient, otp); // cache OTP
        
        // Send OTP based on recipient type
        if (recipientType == OtpEntity.RecipientType.EMAIL) {
            emailService.sendOtpEmail(recipient, otp);
        } else if (recipientType == OtpEntity.RecipientType.PHONE) {
            sendSmsOtp(recipient, otp);
        }
        
        return otpEntity;
    }
    
    /**
     * Overloaded method: Generate OTP using only recipient.
     * Defaults: if recipient contains '@', then EMAIL verification; otherwise PHONE.
     * User ID is set to null.
     */
    public OtpEntity generateOtp(String recipient) {
        OtpEntity.RecipientType type = recipient.contains("@") 
                ? OtpEntity.RecipientType.EMAIL 
                : OtpEntity.RecipientType.PHONE;
        OtpEntity.OtpPurpose purpose = type == OtpEntity.RecipientType.EMAIL 
                ? OtpEntity.OtpPurpose.EMAIL_VERIFICATION 
                : OtpEntity.OtpPurpose.PHONE_VERIFICATION;
        return generateOtp(recipient, type, purpose, null);
    }
    
    /**
     * Retrieve OTP from the cache for a recipient.
     */
    public String getCacheOtp(String recipient) {
        return otpCache.get(recipient);
    }
    
    /**
     * Clear the cached OTP for a recipient.
     */
    public void clearOtp(String recipient) {
        otpCache.remove(recipient);
    }
    
    /**
     * Verify an OTP.
     */
    public boolean verifyOtp(String recipient, String otp) {
        Optional<OtpEntity> otpEntityOpt = otpRepository.findTopByRecipientAndUsedFalseOrderByCreatedAtDesc(recipient);
        
        if (otpEntityOpt.isEmpty()) {
            return false;
        }
        
        OtpEntity otpEntity = otpEntityOpt.get();
        boolean verified = otpEntity.verifyOtp(otp);
        
        if (verified) {
            otpRepository.save(otpEntity);
            clearOtp(recipient); // clear cached OTP if verified
        }
        
        return verified;
    }
    
    /**
     * Generate a random OTP string.
     */
    private String generateRandomOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
    
    /**
     * Send SMS with OTP using Twilio.
     */
    private void sendSmsOtp(String phoneNumber, String otp) {
        String otpMessage = "Your verification code is " + otp + ". Valid for " + EXPIRE_MINUTES + " minutes.";
        
        try {
            twilioConfig.sendSms(phoneNumber, otpMessage);
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
        }
    }
    
    /**
     * Invalidate all previous OTPs for a recipient.
     */
    public void invalidatePreviousOtps(String recipient) {
        otpRepository.findByRecipientAndUsedFalse(recipient)
                .forEach(otpEntity -> {
                    otpEntity.setUsed(true);
                    otpRepository.save(otpEntity);
                });
    }
}
