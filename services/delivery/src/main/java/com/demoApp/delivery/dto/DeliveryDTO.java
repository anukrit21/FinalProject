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
public class DeliveryDTO {
    private Long id;
    
    private Long deliveryPersonId;
    private String deliveryPersonName;
    
    private Long pickupPointId;
    private String pickupPointName;
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String customerPhone;
    
    @NotBlank(message = "Customer email is required")
    private String customerEmail;
    
    private String deliveryAddress;
    private double deliveryLatitude;
    private double deliveryLongitude;
    
    @NotNull(message = "Delivery type is required")
    private Delivery.DeliveryType deliveryType;
    
    private Delivery.DeliveryStatus status;
    
    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;
    
    private LocalDateTime acceptedTime;
    private LocalDateTime pickedUpTime;
    private LocalDateTime deliveredTime;
    
    private double deliveryFee;
    private double extraCharges;
    private String extraChargesReason;
    
    private int deliveryRating;
    private String deliveryFeedback;
    
    private String specialInstructions;

    public Object getDestinationAddress() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDestinationAddress'");
    }

    public int getDestinationLatitude() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDestinationLatitude'");
    }

    public int getDestinationLongitude() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDestinationLongitude'");
    }

    public Object getRecipientName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipientName'");
    }

    public Object getRecipientPhone() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipientPhone'");
    }

    public Object getDeliveryInstructions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDeliveryInstructions'");
    }
}