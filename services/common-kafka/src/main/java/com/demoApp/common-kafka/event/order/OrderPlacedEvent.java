package com.demoApp.kafka.event.order;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderPlacedEvent extends BaseEvent {
    
    private UUID orderId;
    private UUID userId;
    private UUID messId;
    private LocalDateTime orderTime;
    private String status; // PENDING, CONFIRMED, PREPARING, READY, DELIVERED, CANCELED
    private BigDecimal totalAmount;
    private String deliveryOption; // PICKUP, DELIVERY
    private String paymentStatus; // PENDING, PAID, FAILED
    private UUID deliveryAddressId;
    private List<OrderItem> items;
    
    /**
     * Constructor with initialization
     */
    public OrderPlacedEvent(UUID orderId, UUID userId, UUID messId, LocalDateTime orderTime, 
                           String status, BigDecimal totalAmount, String deliveryOption,
                           String paymentStatus, UUID deliveryAddressId, List<OrderItem> items) {
        super();
        init("ORDER_PLACED", "order-service");
        this.orderId = orderId;
        this.userId = userId;
        this.messId = messId;
        this.orderTime = orderTime;
        this.status = status;
        this.totalAmount = totalAmount;
        this.deliveryOption = deliveryOption;
        this.paymentStatus = paymentStatus;
        this.deliveryAddressId = deliveryAddressId;
        this.items = items;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private UUID menuItemId;
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private String specialInstructions;
    }
} 