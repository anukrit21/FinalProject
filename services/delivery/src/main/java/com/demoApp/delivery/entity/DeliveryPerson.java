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
@Table(name = "delivery_persons")
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    
    @Column(unique = true)
    private String mobileNumber;
    
    private String vehicleNumber;
    private String vehicleType;
    
    private boolean isAvailable;
    private boolean isVerified;
    
    @Enumerated(EnumType.STRING)
    private DeliveryZone deliveryZone;
    
    @Enumerated(EnumType.STRING)
    private DeliveryPersonStatus status;
    
    private double rating;
    private int totalDeliveries;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    
    public enum DeliveryZone {
        NORTH_CAMPUS, SOUTH_CAMPUS, EAST_CAMPUS, WEST_CAMPUS, ALL
    }
    
    public enum DeliveryPersonStatus {
        ACTIVE, INACTIVE, SUSPENDED, ON_LEAVE
    }
}
