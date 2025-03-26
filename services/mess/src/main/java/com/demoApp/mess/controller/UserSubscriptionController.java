package com.demoApp.mess.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.demoApp.mess.dto.ApiResponse;
import com.demoApp.mess.dto.UserSubscriptionDTO;
import com.demoApp.mess.entity.UserSubscription;
import com.demoApp.mess.service.AuthService;
import com.demoApp.mess.service.UserSubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {
    
    private final UserSubscriptionService userSubscriptionService;
    private final AuthService authService;   
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getAllUserSubscriptions() {
        List<UserSubscription> subscriptions = userSubscriptionService.getAllUserSubscriptions();
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTOs(subscriptions));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MESS', 'ADMIN')")
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
    
    @GetMapping("/mess/{messId}")
    @PreAuthorize("hasAnyRole('MESS', 'ADMIN')")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getUserSubscriptionsByMessId(@PathVariable Long messId) {
        List<UserSubscription> subscriptions = userSubscriptionService.getUserSubscriptionsByMessId(messId);
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTOs(subscriptions));
    }
    
    @GetMapping("/mess/{messId}/active")
    @PreAuthorize("hasAnyRole('MESS', 'ADMIN')")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getActiveUserSubscriptionsByMessId(@PathVariable Long messId) {
        List<UserSubscription> subscriptions = userSubscriptionService.getActiveUserSubscriptionsByMessId(messId);
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
    
    @GetMapping("/current-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getCurrentUserSubscriptions() {
        Long userId = authService.getCurrentUser().getId();
        List<UserSubscription> subscriptions = userSubscriptionService.getUserSubscriptionsByUserId(userId);
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTOs(subscriptions));
    }
    
    @GetMapping("/current-user/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserSubscriptionDTO.UserSubscriptionResponseDTO>> getCurrentUserActiveSubscriptions() {
        Long userId = authService.getCurrentUser().getId();
        List<UserSubscription> subscriptions = userSubscriptionService.getActiveUserSubscriptionsByUserId(userId);
        return ResponseEntity.ok(userSubscriptionService.convertToResponseDTOs(subscriptions));
    }
} 