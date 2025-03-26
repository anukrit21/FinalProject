package com.demoApp.otp.repository;

import com.demoApp.otp.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for OTP entity
 */
@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    /**
     * Find the latest OTP by recipient, recipient type, and purpose
     */
    @Query("SELECT o FROM OtpEntity o WHERE o.recipient = ?1 AND o.recipientType = ?2 AND o.purpose = ?3 AND o.used = false ORDER BY o.createdAt DESC")
    Optional<OtpEntity> findLatestValidOtp(String recipient, OtpEntity.RecipientType recipientType, OtpEntity.OtpPurpose purpose);

    /**
     * Find OTP by recipient, recipient type, OTP code, and purpose
     */
    Optional<OtpEntity> findByRecipientAndRecipientTypeAndOtpAndPurpose(
            String recipient, 
            OtpEntity.RecipientType recipientType, 
            String otp, 
            OtpEntity.OtpPurpose purpose);

    /**
     * Find all active OTPs for a recipient
     */
    List<OtpEntity> findByRecipientAndRecipientTypeAndUsedFalseAndExpiresAtAfter(
            String recipient, 
            OtpEntity.RecipientType recipientType, 
            LocalDateTime now);
            
    /**
     * Find all unused OTPs for a recipient
     */
    List<OtpEntity> findByRecipientAndUsedFalse(String recipient);

    /**
     * Find the most recent unused OTP for a recipient
     */
    Optional<OtpEntity> findTopByRecipientAndUsedFalseOrderByCreatedAtDesc(String recipient);
    
    /**
     * Find OTP by userId and purpose
     */
    Optional<OtpEntity> findByUserIdAndPurposeAndUsedFalse(Long userId, OtpEntity.OtpPurpose purpose);
    
    /**
     * Find OTP by token
     */
    Optional<OtpEntity> findByToken(String token);

    /**
     * Count OTPs generated for a recipient within a time period
     */
    @Query("SELECT COUNT(o) FROM OtpEntity o WHERE o.recipient = ?1 AND o.recipientType = ?2 AND o.createdAt > ?3 AND o.purpose = ?4")
    int countRecentOtps(String recipient, OtpEntity.RecipientType recipientType, LocalDateTime since, OtpEntity.OtpPurpose purpose);

    /**
     * Find expired but unused OTPs
     */
    List<OtpEntity> findByUsedFalseAndExpiresAtBefore(LocalDateTime now);

    /**
     * Find OTPs by purpose
     */
    List<OtpEntity> findByPurpose(OtpEntity.OtpPurpose purpose);
    
    /**
     * Delete expired OTPs older than a certain date
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpEntity o WHERE o.expiresAt < ?1")
    void deleteExpiredOtps(LocalDateTime cutoffDate);
    
    /**
     * Count failed attempts for a recipient
     */
    @Query("SELECT SUM(o.attempts) FROM OtpEntity o WHERE o.recipient = ?1 AND o.createdAt > ?2")
    Integer countFailedAttempts(String recipient, LocalDateTime since);
} 