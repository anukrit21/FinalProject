package com.demoApp.payment.model;

/**
 * Enum for payment statuses
 */
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    CANCELLED,
    EXPIRED
} 