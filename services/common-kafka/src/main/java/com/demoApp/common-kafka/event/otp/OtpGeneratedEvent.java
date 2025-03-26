package com.demoApp.kafka.event.otp;

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
public class OtpGeneratedEvent extends BaseEvent {
    
    private UUID otpId;
    private String email;
    private String phone;
    private String purpose; // LOGIN, REGISTRATION, PASSWORD_RESET, etc.
    private int expirationMinutes;
    
    /**
     * Constructor with initialization
     */
    public OtpGeneratedEvent(UUID otpId, String email, String phone, String purpose, int expirationMinutes) {
        super();
        init("OTP_GENERATED", "otp-service");
        this.otpId = otpId;
        this.email = email;
        this.phone = phone;
        this.purpose = purpose;
        this.expirationMinutes = expirationMinutes;
    }
} 