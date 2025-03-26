package com.demoApp.mess.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.demoApp.mess.entity.UserSubscription;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;
    
    private UserSubscription.SubscriptionStatus status = UserSubscription.SubscriptionStatus.ACTIVE;
    
    private boolean paymentCompleted = false;
    
    private String paymentTransactionId;
    
    private String mealDeliveryAddress;
    
    private String mealDeliveryInstructions;
    
    // DTO for responses that includes the subscription details
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSubscriptionResponseDTO {
        private Long id;
        private Long userId;
        private SubscriptionDTO subscription;
        private LocalDate startDate;
        private LocalDate endDate;
        private UserSubscription.SubscriptionStatus status;
        private boolean paymentCompleted;
        private String paymentTransactionId;
        private String mealDeliveryAddress;
        private String mealDeliveryInstructions;
        private long daysRemaining;
        private boolean active;
    }
} 