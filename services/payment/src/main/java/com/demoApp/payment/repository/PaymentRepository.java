package com.demoApp.payment.repository;

import com.demoApp.payment.entity.Payment;
import com.demoApp.payment.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by payment ID
     */
    Optional<Payment> findByPaymentId(String paymentId);

    /**
     * Find payment by payment provider reference
     */
    Optional<Payment> findByPaymentProviderReference(String paymentProviderReference);

    /**
     * Find all payments by user ID
     */
    Page<Payment> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all payments by merchant ID
     */
    Page<Payment> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * Find all payments by owner ID
     */
    Page<Payment> findByOwnerId(Long ownerId, Pageable pageable);

    /**
     * Find all payments by user ID and status
     */
    Page<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status, Pageable pageable);

    /**
     * Find all payments by merchant ID and status
     */
    Page<Payment> findByMerchantIdAndStatus(Long merchantId, PaymentStatus status, Pageable pageable);

    /**
     * Find all payments by status
     */
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    /**
     * Find all payments created between dates
     */
    Page<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find all payments by user ID and created between dates
     */
    Page<Payment> findByUserIdAndCreatedAtBetween(
            Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find all payments by merchant ID and created between dates
     */
    Page<Payment> findByMerchantIdAndCreatedAtBetween(
            Long merchantId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Calculate total amount of completed payments for a user
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    BigDecimal calculateTotalCompletedAmountForUser(@Param("userId") Long userId);

    /**
     * Calculate total amount of completed payments for a merchant
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.merchantId = :merchantId AND p.status = 'COMPLETED'")
    BigDecimal calculateTotalCompletedAmountForMerchant(@Param("merchantId") Long merchantId);

    /**
     * Find recent payments by user
     */
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findRecentPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find recent payments by merchant
     */
    @Query("SELECT p FROM Payment p WHERE p.merchantId = :merchantId ORDER BY p.createdAt DESC")
    List<Payment> findRecentPaymentsByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    /**
     * Find payments by order reference
     */
    List<Payment> findByOrderReference(String orderReference);

    /**
     * Find payments by product ID
     */
    Page<Payment> findByProductId(Long productId, Pageable pageable);

    /**
     * Find payments by subscription ID
     */
    Page<Payment> findBySubscriptionId(Long subscriptionId, Pageable pageable);
} 