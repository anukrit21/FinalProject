package com.demoApp.payment.dto;

import com.demoApp.payment.model.PaymentMethod;
import com.demoApp.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private String paymentId;
    private Long userId;
    private String description;
    private BigDecimal amount;
    private BigDecimal tax;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String paymentProviderReference;
    private String orderReference;
    private Long productId;
    private Long subscriptionId;
    private Long merchantId;
    private Long ownerId;
    private String customerName;
    private String customerEmail;
    private BigDecimal refundAmount;
    private String refundReason;
    private String refundId;
    private LocalDateTime refundDate;
    private String receiptUrl;
    private String invoiceUrl;
    private String failureMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    
    // For client-side processing
    private String clientSecret;
    private String paymentIntentId;
    private String setupIntentId;
    private boolean requiresAction;
    private String nextAction;
} 