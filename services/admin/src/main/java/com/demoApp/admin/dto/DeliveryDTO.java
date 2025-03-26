package com.demoApp.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for representing delivery information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {

    private Long id;

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String userName;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotBlank(message = "Status is required")
    private String status;

    private Long courierId;

    private String courierName;

    private String trackingNumber;

    private LocalDateTime scheduledDate;

    private LocalDateTime actualDeliveryDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String contactPhone;

    private String contactEmail;

    private String deliveryInstructions;

    private String proofOfDeliveryUrl;
} 