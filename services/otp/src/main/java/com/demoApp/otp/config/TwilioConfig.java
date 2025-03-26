package com.demoApp.otp.config;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Twilio Configuration
 * Generate own account sid, authToken, trialNumber
 * from https://console.twilio.com/
 */
@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioConfig {
    private String accountSid;
    private String authToken;
    private String trialNumber;
    
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
    
    /**
     * Send SMS via Twilio
     * @param to recipient phone number
     * @param message message content
     * @return message SID
     */
    public String sendSms(String to, String message) {
        Message twilioMessage = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(trialNumber),
                message
        ).create();
        
        return twilioMessage.getSid();
    }
}