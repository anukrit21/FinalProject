package com.demoApp.delivery.dto;

import com.demoApp.delivery.entity.Delivery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDTO {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Pickup point ID is required")
    private Long pickupPointId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String customerPhone;
    
    @NotBlank(message = "Customer email is required")
    private String customerEmail;
    
    // For on-demand delivery to home
    private String deliveryAddress;
    private double deliveryLatitude;
    private double deliveryLongitude;
    
    @NotNull(message = "Delivery type is required")
    private Delivery.DeliveryType deliveryType;
    
    // For subscription deliveries
    @NotNull(message = "Scheduled time is required for deliveries")
    private LocalDateTime scheduledTime;
    
    private String specialInstructions;
} 