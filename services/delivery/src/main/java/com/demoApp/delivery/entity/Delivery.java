package com.demoApp.delivery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;
    
    @ManyToOne
    @JoinColumn(name = "pickup_point_id")
    private PickupPoint pickupPoint;
    
    private Long orderId;
    private Long userId;
    
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    
    private String deliveryAddress;
    private double deliveryLatitude;
    private double deliveryLongitude;
    
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    
    private LocalDateTime scheduledTime;
    private LocalDateTime acceptedTime;
    private LocalDateTime pickedUpTime;
    private LocalDateTime deliveredTime;
    private LocalDateTime assignedAt;
    private LocalDateTime cancelledTime;

    private double deliveryFee;
    private double extraCharges; // Extra charges for on-demand home delivery
    private String extraChargesReason;
    
    private int deliveryRating;
    private String deliveryFeedback;
    
    private String specialInstructions;
    
    // ✅ Add createdAt field
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Automatically set createdAt before persisting the entity
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum DeliveryType {
        SUBSCRIPTION, ON_DEMAND
    }
    
    public enum DeliveryStatus {
        PENDING, ASSIGNED, ACCEPTED, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED, FAILED
    }
    

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    public LocalDateTime getPickedUpAt(LocalDateTime pickedUpAt) {
        return pickedUpAt;
    }

    public void setCancelledAt(LocalDateTime cancelledTime) {
        this.cancelledTime = cancelledTime;
    }
    
    

    public void setDeliveredAt(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }
    
    public void setInTransitAt(LocalDateTime inTransitTime) {
        // You can either use a dedicated field or reuse pickedUpTime, depending on design
        this.pickedUpTime = inTransitTime;
    }
    
    public void setPickedUpAt(LocalDateTime pickedUpTime) {
        this.pickedUpTime = pickedUpTime;
    }
    

    public void setDestinationAddress(Object destinationAddress) {
        this.deliveryAddress = destinationAddress.toString();
    }
    
    public void setDestinationLatitude(int destinationLatitude) {
        this.deliveryLatitude = destinationLatitude;
    }
    
    public void setDestinationLongitude(int destinationLongitude) {
        this.deliveryLongitude = destinationLongitude;
    }
    
    public void setRecipientName(Object recipientName) {
        this.customerName = recipientName.toString();
    }
    
    public void setRecipientPhone(Object recipientPhone) {
        this.customerPhone = recipientPhone.toString();
    }
    
    public void setDeliveryInstructions(Object deliveryInstructions) {
        this.specialInstructions = deliveryInstructions.toString();
    }
}    