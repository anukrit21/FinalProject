package com.demoApp.owner.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.demoApp.owner.client.fallback.MessServiceFallback;
import com.demoApp.owner.config.FeignClientConfig;

@FeignClient(
    name = "mess-service", 
    fallback = MessServiceFallback.class,
    configuration = FeignClientConfig.class
)
public interface MessServiceClient {
    
    @GetMapping("/api/v1/mess/owner/{ownerId}")
    ResponseEntity<Object> getMessesByOwnerId(@PathVariable Long ownerId);
    
    @GetMapping("/api/v1/mess/{messId}")
    ResponseEntity<Object> getMessDetails(@PathVariable Long messId);
}