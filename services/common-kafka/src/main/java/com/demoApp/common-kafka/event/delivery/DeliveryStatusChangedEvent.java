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
public class DeliveryStatusChangedEvent extends BaseEvent {
    
    private UUID deliveryId;
    private UUID orderId;
    private UUID userId;
    private UUID deliveryPersonId;
    private String previousStatus;
    private String newStatus; // ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED
    private LocalDateTime statusChangeTime;
    private LocalDateTime newEstimatedDeliveryTime;
    private GeoLocation currentLocation;
    private String statusChangeReason;
    
    /**
     * Constructor with initialization
     */
    public DeliveryStatusChangedEvent(UUID deliveryId, UUID orderId, UUID userId, UUID deliveryPersonId,
                                    String previousStatus, String newStatus, LocalDateTime statusChangeTime,
                                    LocalDateTime newEstimatedDeliveryTime, GeoLocation currentLocation,
                                    String statusChangeReason) {
        super();
        init("DELIVERY_STATUS_CHANGED", "delivery-service");
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.userId = userId;
        this.deliveryPersonId = deliveryPersonId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.statusChangeTime = statusChangeTime;
        this.newEstimatedDeliveryTime = newEstimatedDeliveryTime;
        this.currentLocation = currentLocation;
        this.statusChangeReason = statusChangeReason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoLocation {
        private Double latitude;
        private Double longitude;
        private String locationName;
        private LocalDateTime capturedAt;
    }
} 