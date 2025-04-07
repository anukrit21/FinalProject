package com.demoApp.mess.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonDTO {
    private Long id;
    
    
    // Basic information
    private String name;
    private String email;
    private String phone;
    
    // Associated mess details
    private Long messId;
    private String messName;
    
    // Identification details (added to resolve getIdType() and getIdNumber() errors)
    private String idType;
    private String idNumber;
    
    // Status
    private boolean active;
    
    // Rating information
    private Double averageRating;
    private Integer totalRatings;
    
    // Media URLs
    private String profileImageUrl;
    private String idProofUrl;
    
    // Additional information
    private String vehicleNumber;
    private String vehicleType;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    
    // Last known location
    private Double lastLatitude;
    private Double lastLongitude;
    private LocalDateTime lastLocationUpdateTime;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
