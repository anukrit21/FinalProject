package com.demoApp.owner.client.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.demoApp.owner.client.SubscriptionService;

@Component
public class SubscriptionServiceFallback implements SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceFallback.class);
    
    @Override
    public ResponseEntity<Object> getSubscriptionsByOwnerId(Long ownerId) {
        log.error("Fallback: Unable to connect to subscription service for owner ID: {}", ownerId);
        return ResponseEntity.ok().build();
    }
} 