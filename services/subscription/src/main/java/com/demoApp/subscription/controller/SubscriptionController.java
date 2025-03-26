package com.demoApp.subscription.controller;

import com.demoApp.subscription.dto.ApiResponse;
import com.demoApp.subscription.dto.SubscriptionDTO;
import com.demoApp.subscription.entity.Subscription;
import com.demoApp.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptionService.convertToDTOs(subscriptions));
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionDTO>> getActiveSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getActiveSubscriptions();
        return ResponseEntity.ok(subscriptionService.convertToDTOs(subscriptions));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable Long id) {
        Subscription subscription = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(subscriptionService.convertToDTO(subscription));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDTO> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        Subscription subscription = subscriptionService.createSubscription(subscriptionDTO);
        return new ResponseEntity<>(subscriptionService.convertToDTO(subscription), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDTO> updateSubscription(
            @PathVariable Long id, 
            @Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        Subscription subscription = subscriptionService.updateSubscription(id, subscriptionDTO);
        return ResponseEntity.ok(subscriptionService.convertToDTO(subscription));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok(new ApiResponse(true, "Subscription deleted successfully"));
    }
    
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDTO> toggleSubscriptionStatus(@PathVariable Long id) {
        Subscription subscription = subscriptionService.toggleSubscriptionStatus(id);
        return ResponseEntity.ok(subscriptionService.convertToDTO(subscription));
    }
} 