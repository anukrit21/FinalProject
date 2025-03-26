package com.demoApp.kafka.event.delivery;

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
public class DeliveryAssignedEvent extends BaseEvent {
    
    private UUID deliveryId;
    private UUID orderId;
    private UUID userId;
    private UUID deliveryPersonId;
    private UUID messId;
    private String deliveryStatus; // ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED
    private LocalDateTime assignedTime;
    private LocalDateTime estimatedDeliveryTime;
    private DeliveryAddress deliveryAddress;
    private String specialInstructions;
    
    /**
     * Constructor with initialization
     */
    public DeliveryAssignedEvent(UUID deliveryId, UUID orderId, UUID userId, UUID deliveryPersonId,
                               UUID messId, String deliveryStatus, LocalDateTime assignedTime,
                               LocalDateTime estimatedDeliveryTime, DeliveryAddress deliveryAddress,
                               String specialInstructions) {
        super();
        init("DELIVERY_ASSIGNED", "delivery-service");
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.userId = userId;
        this.deliveryPersonId = deliveryPersonId;
        this.messId = messId;
        this.deliveryStatus = deliveryStatus;
        this.assignedTime = assignedTime;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.deliveryAddress = deliveryAddress;
        this.specialInstructions = specialInstructions;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryAddress {
        private UUID addressId;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String landmark;
        private Double latitude;
        private Double longitude;
    }
} 