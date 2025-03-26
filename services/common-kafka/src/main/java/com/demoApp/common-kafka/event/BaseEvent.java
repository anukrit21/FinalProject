package com.demoApp.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent implements Serializable {
    
    private UUID eventId;
    private LocalDateTime timestamp;
    private String eventType;
    private String source;
    
    /**
     * Initialize with default values
     */
    protected void init(String eventType, String source) {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.source = source;
    }
} 