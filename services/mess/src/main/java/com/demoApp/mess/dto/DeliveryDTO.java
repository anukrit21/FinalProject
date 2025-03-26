package com.demoApp.mess.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.demoApp.mess.enums.DeliveryStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {
    private Long id;
    
    // Reference to the order
    private Long orderReferenceId;
    private String orderReferenceType; // Subscription, Menu, etc.
    
    // Relations
    private Long customerId;
    private String customerName;
    private String customerPhone;
    
    private Long messId;
    private String messName;
    private String messPhone;
    
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    
    // Delivery location
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPostalCode;
    private String deliveryInstructions;
    
    // Contact info
    private String contactName;
    private String contactPhone;
    
    // Coordinates
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private Double currentLatitude;
    private Double currentLongitude;
    
    // Scheduled times
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime scheduledDeliveryTime;
    
    // Actual times
    private LocalDateTime actualPickupTime;
    private LocalDateTime actualDeliveryTime;
    
    // Status
    private DeliveryStatus status;
    
    // Tracking
    private String trackingCode;
    
    // Feedback
    private Integer rating;
    private String feedback;
    
    // Issues
    private String issueDescription;
    private String resolution;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
} 