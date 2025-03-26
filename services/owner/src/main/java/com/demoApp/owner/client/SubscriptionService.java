package com.demoApp.owner.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.demoApp.owner.client.fallback.SubscriptionServiceFallback;
import com.demoApp.owner.config.FeignClientConfig;

@FeignClient(
    name = "subscription-service", 
    fallback = SubscriptionServiceFallback.class,
    configuration = FeignClientConfig.class
)
public interface SubscriptionService {
    
    @GetMapping("/api/v1/subscriptions/owner/{ownerId}")
    ResponseEntity<Object> getSubscriptionsByOwnerId(@PathVariable Long ownerId);
}