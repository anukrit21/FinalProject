package com.demoApp.delivery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pickup_points")
public class PickupPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    private String address;
    private double latitude;
    private double longitude;
    
    @Enumerated(EnumType.STRING)
    private CampusZone campusZone;
    
    // Operating hours
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    // For lunch delivery time window (fixed at 1 PM as per requirements)
    private LocalTime lunchDeliveryStart;
    private LocalTime lunchDeliveryEnd;
    
    // For dinner delivery time window (fixed at 8 PM as per requirements)
    private LocalTime dinnerDeliveryStart;
    private LocalTime dinnerDeliveryEnd;
    
    private boolean isActive;
    
    public enum CampusZone {
        NORTH_CAMPUS, SOUTH_CAMPUS, EAST_CAMPUS, WEST_CAMPUS
    }
} 