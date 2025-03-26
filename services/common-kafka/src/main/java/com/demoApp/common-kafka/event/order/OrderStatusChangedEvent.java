package com.demoApp.kafka.event.order;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderStatusChangedEvent extends BaseEvent {
    
    private UUID orderId;
    private UUID userId;
    private UUID messId;
    private String previousStatus;
    private String newStatus; // PENDING, CONFIRMED, PREPARING, READY, DELIVERED, CANCELED
    private LocalDateTime statusChangeTime;
    private String statusChangeReason;
    private String changedBy; // USER, SYSTEM, ADMIN, MESS_OWNER
    
    /**
     * Constructor with initialization
     */
    public OrderStatusChangedEvent(UUID orderId, UUID userId, UUID messId, 
                                 String previousStatus, String newStatus, 
                                 LocalDateTime statusChangeTime, String statusChangeReason,
                                 String changedBy) {
        super();
        init("ORDER_STATUS_CHANGED", "order-service");
        this.orderId = orderId;
        this.userId = userId;
        this.messId = messId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.statusChangeTime = statusChangeTime;
        this.statusChangeReason = statusChangeReason;
        this.changedBy = changedBy;
    }
} 