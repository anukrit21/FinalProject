package com.demoApp.mess.enums;

/**
 * Enum representing the status of a delivery
 */
public enum DeliveryStatus {
    /**
     * Delivery has been created but not yet assigned to a delivery person
     */
    PENDING,
    
    /**
     * Delivery has been assigned to a delivery person, not yet picked up
     */
    ASSIGNED,
    
    /**
     * Delivery has been picked up from mess
     */
    PICKED_UP,
    
    /**
     * Delivery is in transit to the customer
     */
    IN_TRANSIT,
    
    /**
     * Delivery has been successfully delivered to the customer
     */
    DELIVERED,
    
    /**
     * Delivery has failed due to issues (address not found, customer not available, etc.)
     */
    FAILED,
    
    /**
     * Delivery has been cancelled by customer, mess, or admin
     */
    CANCELLED
} 