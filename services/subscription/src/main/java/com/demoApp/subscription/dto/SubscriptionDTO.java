package com.demoApp.subscription.dto;

import com.demoApp.subscription.entity.Subscription;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Subscription type is required")
    private Subscription.SubscriptionType type;
    
    private Subscription.Status status = Subscription.Status.ACTIVE;
    
    @NotNull(message = "Subscription period is required")
    private Subscription.Period subscriptionPeriod;
    
    @NotNull(message = "Meal type is required")
    private Subscription.MealType mealType;
    
    private String office;
    
    private Integer mealPerPortion;
    
    private Integer mealsPerWeek;
    
    private String deliveryDays;
} 