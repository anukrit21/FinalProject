package com.demoApp.subscription.dto;

import com.demoApp.subscription.entity.UserSubscription;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate endDate;
    
    @NotNull(message = "Status is required")
    private UserSubscription.SubscriptionStatus status;
    
    private boolean paymentCompleted = false;
    
    private String paymentTransactionId;
    
    private String mealDeliveryAddress;
    
    private String mealDeliveryInstructions;

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