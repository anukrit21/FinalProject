package com.demoApp.subscription.controller;

import com.demoApp.subscription.dto.ApiResponse;
import com.demoApp.subscription.dto.UserSubscriptionDTO;
import com.demoApp.subscription.entity.UserSubscription;
import com.demoApp.subscription.service.UserSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {
    
    private final UserSubscriptionService userSubscriptionService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getAllUserSubscriptions() {
        List<UserSubscription> subscriptions = userSubscriptionService.getAllUserSubscriptions();
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTOs(subscriptions));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserSubscriptionDTO.UserSubscriptionResponseDTO> getUserSubscriptionById(@PathVariable Long id) {
        UserSubscription subscription = userSubscriptionService.getUserSubscriptionById(id);
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTO(subscription));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getUserSubscriptionsByUserId(@PathVariable Long userId) {
        List<UserSubscription> subscriptions = userSubscriptionService.getUserSubscriptionsByUserId(userId);
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTOs(subscriptions));
    }
    
    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getActiveUserSubscriptionsByUserId(@PathVariable Long userId) {
        List<UserSubscription> subscriptions = userSubscriptionService.getActiveUserSubscriptionsByUserId(userId);
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTOs(subscriptions));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserSubscriptionDTO.UserSubscriptionResponseDTO> createUserSubscription(@Valid @RequestBody UserSubscriptionDTO userSubscriptionDTO) {
        UserSubscription subscription = userSubscriptionService.createUserSubscription(userSubscriptionDTO);
        return new ResponseEntity<>(userSubscriptionService.convertToResponseDTO(subscription), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserSubscriptionDTO.UserSubscriptionResponseDTO> updateUserSubscription(
            @PathVariable Long id, 
            @Valid @RequestBody UserSubscriptionDTO userSubscriptionDTO) {
        UserSubscription subscription = userSubscriptionService.updateUserSubscription(id, userSubscriptionDTO);
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTO(subscription));
    }
    
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> cancelUserSubscription(@PathVariable Long id) {
        try {
            userSubscriptionService.cancelUserSubscription(id);
            return ResponseEntity.ok(new ApiResponse(true, "User subscription cancelled successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUserSubscription(@PathVariable Long id) {
        userSubscriptionService.deleteUserSubscription(id);
        return ResponseEntity.ok(new ApiResponse(true, "User subscription deleted successfully"));
    }
    
    @GetMapping("/check-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> checkAndUpdateExpiredSubscriptions() {
        userSubscriptionService.updateExpiredSubscriptions();
        return ResponseEntity.ok(new ApiResponse(true, "Expired subscriptions checked and updated"));
    }
} 