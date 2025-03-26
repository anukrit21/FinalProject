package com.demoApp.delivery.dto;

import com.demoApp.delivery.entity.PickupPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupPointDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private double latitude;
    private double longitude;
    
    @NotNull(message = "Campus zone is required")
    private PickupPoint.CampusZone campusZone;
    
    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;
    
    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;
    
    @NotNull(message = "Lunch delivery start time is required")
    private LocalTime lunchDeliveryStart;
    
    @NotNull(message = "Lunch delivery end time is required")
    private LocalTime lunchDeliveryEnd;
    
    @NotNull(message = "Dinner delivery start time is required")
    private LocalTime dinnerDeliveryStart;
    
    @NotNull(message = "Dinner delivery end time is required")
    private LocalTime dinnerDeliveryEnd;
    
    private boolean isActive;
} 