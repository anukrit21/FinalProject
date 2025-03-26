package com.demoApp.payment.entity;

import com.demoApp.payment.model.PaymentMethod;
import com.demoApp.payment.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for payment records
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal tax;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_provider_reference")
    private String paymentProviderReference;

    private String orderReference;

    private Long productId;

    private Long subscriptionId;

    private Long merchantId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    private String customerName;

    private String customerEmail;

    private String billingAddress;

    private String shippingAddress;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "refund_id")
    private String refundId;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "failure_message")
    private String failureMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "failure_code")
    private String failureCode;

    @Column(name = "is_test", nullable = false)
    private boolean isTest = false;

    public void markAsCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markAsFailed(String failureMessage, String failureCode) {
        this.status = PaymentStatus.FAILED;
        this.failureMessage = failureMessage;
        this.failureCode = failureCode;
    }

    public void markAsRefunded(BigDecimal refundAmount, String refundReason, String refundId) {
        this.status = (refundAmount.compareTo(this.amount) >= 0) ? 
                PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
        this.refundId = refundId;
        this.refundDate = LocalDateTime.now();
    }
} 