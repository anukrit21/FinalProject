package com.demoApp.otp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entity for storing OTP (One-Time Password) information
 */
@Entity
@Table(name = "otps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(name = "recipient_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipientType recipientType;

    @Column(nullable = false)
    private String otp;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "purpose", nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpPurpose purpose;

    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;
    
    @Column(name = "user_id")
    private Long userId;
    
    private String token;
    
    @Builder.Default
    private int maxRetries = 3;
    
    @Builder.Default
    private boolean verified = false;
    
    @Builder.Default
    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    /**
     * Enum for different OTP purposes
     */
    public enum OtpPurpose {
        REGISTRATION,
        PASSWORD_RESET,
        LOGIN,
        EMAIL_VERIFICATION,
        PHONE_VERIFICATION,
        TRANSACTION_AUTHORIZATION
    }

    /**
     * Enum for recipient types
     */
    public enum RecipientType {
        EMAIL,
        PHONE
    }

    /**
     * Check if the OTP is expired
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if the OTP is valid (not expired and not used)
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return !isExpired() && !used;
    }

    /**
     * Mark the OTP as used
     */
    public void markAsUsed() {
        this.used = true;
        this.verified = true;
        this.verifiedAt = LocalDateTime.now();
    }

    /**
     * Increment retry count
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    /**
     * Increment attempts count when OTP is checked
     */
    public void incrementAttempts() {
        this.attempts++;
    }
    
    /**
     * Check if maximum retries exceeded
     * @return true if maximum retries exceeded, false otherwise
     */
    public boolean isMaxRetriesExceeded() {
        return this.retryCount >= this.maxRetries;
    }
    
    /**
     * Check if maximum attempts exceeded
     * @return true if maximum attempts exceeded, false otherwise
     */
    public boolean isMaxAttemptsExceeded() {
        return this.attempts >= this.maxRetries;
    }
    
    /**
     * Get remaining time until expiry in seconds
     * @return seconds until expiry, 0 if already expired
     */
    public long getRemainingTimeInSeconds() {
        if (isExpired()) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), expiresAt);
    }
    
    /**
     * Verify OTP against provided value
     * @param otpValue OTP value to check
     * @return true if matched, false otherwise
     */
    public boolean verifyOtp(String otpValue) {
        incrementAttempts();
        if (!isValid()) {
            return false;
        }
        
        boolean isMatch = this.otp.equals(otpValue);
        if (isMatch) {
            markAsUsed();
        } else if (isMaxAttemptsExceeded()) {
            // Auto expire OTP after max attempts
            this.expiresAt = LocalDateTime.now();
        }
        return isMatch;
    }
    
    /**
     * Reset retry count
     */
    public void resetRetryCount() {
        this.retryCount = 0;
    }
    
    @PrePersist
    protected void onCreate() {
        if (expiresAt == null) {
            // Default expiry of 5 minutes if not set
            expiresAt = LocalDateTime.now().plusMinutes(5);
        }
    }
} 