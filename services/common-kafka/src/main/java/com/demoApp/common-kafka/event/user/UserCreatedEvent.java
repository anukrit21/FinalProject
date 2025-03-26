package com.demoApp.kafka.event.user;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCreatedEvent extends BaseEvent {
    
    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    
    /**
     * Constructor with initialization
     */
    public UserCreatedEvent(UUID userId, String username, String email, String fullName, String role) {
        super();
        init("USER_CREATED", "user-service");
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
} 