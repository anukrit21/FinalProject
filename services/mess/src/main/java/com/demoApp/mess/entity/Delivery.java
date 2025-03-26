package com.demoApp.mess.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.demoApp.mess.enums.DeliveryStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Tracking code (unique identifier for customers to track their delivery)
    @Column(unique = true)
    private String trackingCode;
    
    // Reference to the order - could be a subscription, direct order, or other
    @Column(nullable = false)
    private Long orderReferenceId;
    
    @Column(nullable = false)
    private String orderReferenceType; // "SUBSCRIPTION", "MENU", etc.
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mess_id", nullable = false)
    private User mess;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;
    
    // Delivery location
    @Column(nullable = false)
    private String deliveryAddress;
    
    @Column
    private String deliveryCity;
    
    @Column
    private String deliveryState;
    
    @Column
    private String deliveryPostalCode;
    
    @Column(columnDefinition = "TEXT")
    private String deliveryInstructions;
    
    // Contact information
    @Column(nullable = false)
    private String contactName;
    
    @Column(nullable = false)
    private String contactPhone;
    
    // Coordinates for pickup and delivery
    @Column
    private Double pickupLatitude;
    
    @Column
    private Double pickupLongitude;
    
    @Column
    private Double deliveryLatitude;
    
    @Column
    private Double deliveryLongitude;
    
    // Current location (for tracking)
    @Column
    private Double currentLatitude;
    
    @Column
    private Double currentLongitude;
    
    // Scheduled and actual timestamps
    @Column
    private LocalDateTime scheduledPickupTime;
    
    @Column
    private LocalDateTime scheduledDeliveryTime;
    
    @Column
    private LocalDateTime actualPickupTime;
    
    @Column
    private LocalDateTime actualDeliveryTime;
    
    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;
    
    // Feedback from customer
    @Column
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    // Issue handling
    @Column(columnDefinition = "TEXT")
    private String issueDescription;
    
    @Column(columnDefinition = "TEXT")
    private String resolution;
    
    // Audit fields
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(updatable = false)
    private Long createdBy;
    
    @Column
    private Long updatedBy;
    
    // Lifecycle callback - generate tracking code if not present
    @PrePersist
    protected void onCreate() {
        if (trackingCode == null || trackingCode.isEmpty()) {
            // Generate a random tracking code with format: PREFIX-RANDOM-TIMESTAMP
            String prefix = "DLV";
            String randomPart = Long.toString(Math.abs(System.nanoTime() % 10000));
            String timestamp = Long.toString(System.currentTimeMillis() / 1000).substring(6);
            
            this.trackingCode = prefix + "-" + randomPart + "-" + timestamp;
        }
    }
} 