package com.demoApp.kafka.event.notification;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationSentEvent extends BaseEvent {
    
    private UUID notificationId;
    private UUID userId;
    private String notificationType; // ORDER_UPDATE, PAYMENT_CONFIRMATION, DELIVERY_UPDATE, PROMOTION, SYSTEM
    private String channel; // EMAIL, SMS, PUSH, IN_APP
    private String templateId;
    private Map<String, String> templateData;
    private String title;
    private String message;
    private String status; // SENT, FAILED, DELIVERED, READ
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private String failureReason;
    private UUID relatedEntityId; // order_id, payment_id, etc.
    private String relatedEntityType; // ORDER, PAYMENT, DELIVERY, etc.
    
    /**
     * Constructor with initialization
     */
    public NotificationSentEvent(UUID notificationId, UUID userId, String notificationType,
                               String channel, String templateId, Map<String, String> templateData,
                               String title, String message, String status, LocalDateTime sentAt,
                               LocalDateTime deliveredAt, LocalDateTime readAt, String failureReason,
                               UUID relatedEntityId, String relatedEntityType) {
        super();
        init("NOTIFICATION_SENT", "notification-service");
        this.notificationId = notificationId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.channel = channel;
        this.templateId = templateId;
        this.templateData = templateData;
        this.title = title;
        this.message = message;
        this.status = status;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
        this.readAt = readAt;
        this.failureReason = failureReason;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
    }
} 