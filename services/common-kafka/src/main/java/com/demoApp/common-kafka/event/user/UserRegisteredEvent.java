package com.demoApp.kafka.event.user;

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
public class UserRegisteredEvent extends BaseEvent {
    
    private UUID userId;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String userRole; // USER, MESS_OWNER, DELIVERY_PERSON
    private boolean emailVerified;
    private boolean phoneVerified;
    private String registrationSource; // APP, WEB, SOCIAL_MEDIA, REFERRAL
    private String registrationPlatform; // ANDROID, IOS, WEB
    private String registrationIp;
    private String userAgent;
    private LocalDateTime registrationDate;
    private String referralCode;
    private UUID referredBy;
    
    /**
     * Constructor with initialization
     */
    public UserRegisteredEvent(UUID userId, String email, String phone, String firstName, String lastName,
                             String userRole, boolean emailVerified, boolean phoneVerified,
                             String registrationSource, String registrationPlatform,
                             String registrationIp, String userAgent, 
                             LocalDateTime registrationDate, String referralCode, UUID referredBy) {
        super();
        init("USER_REGISTERED", "user-service");
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
        this.registrationSource = registrationSource;
        this.registrationPlatform = registrationPlatform;
        this.registrationIp = registrationIp;
        this.userAgent = userAgent;
        this.registrationDate = registrationDate;
        this.referralCode = referralCode;
        this.referredBy = referredBy;
    }
} 