package com.demoApp.kafka.event.payment;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentProcessedEvent extends BaseEvent {
    
    private UUID paymentId;
    private UUID userId;
    private UUID subscriptionId;
    private String paymentMethod;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String transactionId;
    
    /**
     * Constructor with initialization
     */
    public PaymentProcessedEvent(UUID paymentId, UUID userId, UUID subscriptionId, 
                                String paymentMethod, BigDecimal amount, String currency, 
                                String status, String transactionId) {
        super();
        init("PAYMENT_PROCESSED", "payment-service");
        this.paymentId = paymentId;
        this.userId = userId;
        this.subscriptionId = subscriptionId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.transactionId = transactionId;
    }
} 