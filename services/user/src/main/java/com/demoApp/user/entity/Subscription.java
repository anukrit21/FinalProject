package com.demoApp.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long subscribedToId;
    
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime validUntil;
    
    @Builder.Default
    private boolean active = true;
    
    public enum SubscriptionType {
        FREE, BASIC, PREMIUM, VIP
    }
} 