package com.demoApp.mess.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.demoApp.mess.entity.Subscription;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private Subscription.SubscriptionType type;
    private boolean active;
    private Long messId;
    private String messName;
    private String imageUrl;
    private Integer mealsPerWeek;
    private String deliveryDays;
    private String mealTypes;
    private String dietaryOptions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
