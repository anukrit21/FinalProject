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

    public void setCancelledAt(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCancelledAt'");
    }

    public void setDeliveredAt(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDeliveredAt'");
    }

    public void setInTransitAt(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setInTransitAt'");
    }

    public void setPickedUpAt(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPickedUpAt'");
    }

    public void setDestinationAddress(Object destinationAddress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDestinationAddress'");
    }

    public void setDestinationLatitude(int destinationLatitude) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDestinationLatitude'");
    }

    public void setDestinationLongitude(int destinationLongitude) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDestinationLongitude'");
    }

    public void setRecipientName(Object recipientName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRecipientName'");
    }

    public void setRecipientPhone(Object recipientPhone) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRecipientPhone'");
    }

    public void setDeliveryInstructions(Object deliveryInstructions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDeliveryInstructions'");
    }   
   
    
}
