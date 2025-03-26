package com.demoApp.mess.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    public enum SubscriptionType {
        DAILY, WEEKLY, MONTHLY, CUSTOM
    }
    
    public enum MealType {
        BREAKFAST, LUNCH, DINNER, ALL
    }
    
    public enum Status {
        ACTIVE, INACTIVE, PENDING, EXPIRED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType type;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mess_id", nullable = false)
    private Mess mess;
    
    @Column(name = "user_id")
    private Long userId;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    // New fields
    private String imageUrl;
    
    // Fields for subscription details
    private Integer mealsPerWeek;
    
    private String deliveryDays;
    
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    
    private String dietaryOptions; // Veg, Non-veg, etc.
    
    private Integer mealPerPortion;
    
    private String office;
    
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setMealTypes(String mealTypes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMealTypes'");
    }
}
