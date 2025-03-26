package com.demoApp.payment.controller;

import com.demoApp.payment.dto.ApiResponse;
import com.demoApp.payment.dto.PaymentRequestDTO;
import com.demoApp.payment.dto.PaymentResponseDTO;
import com.demoApp.payment.dto.RefundRequestDTO;
import com.demoApp.payment.exception.PaymentException;
import com.demoApp.payment.exception.ResourceNotFoundException;
import com.demoApp.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for payment operations
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a payment
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO, 
                                        Authentication authentication) {
        try {
            // Security check: ensure the authenticated user is the same as the user making the payment
            String userId = authentication.getName();
            if (!userId.equals(paymentRequestDTO.getUserId().toString())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to make payments for other users", null));
            }
            
            PaymentResponseDTO responseDTO = paymentService.processPayment(paymentRequestDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Payment processed successfully", responseDTO));
        } catch (PaymentException e) {
            log.error("Payment processing error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error during payment processing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPaymentById(@PathVariable String paymentId, Authentication authentication) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentById(paymentId);
            
            // Security check: ensure the authenticated user is the same as the payment's user
            // or has owner/merchant/admin role
            String userId = authentication.getName();
            if (!userId.equals(payment.getUserId().toString()) &&
                    !userId.equals(payment.getOwnerId() != null ? payment.getOwnerId().toString() : "") &&
                    !userId.equals(payment.getMerchantId() != null ? payment.getMerchantId().toString() : "") &&
                    !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to view this payment", null));
            }
            
            return ResponseEntity.ok(new ApiResponse(true, "Payment found", payment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error retrieving payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Get payments by user ID
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPaymentsByUserId(@PathVariable Long userId, 
                                             Pageable pageable, 
                                             Authentication authentication) {
        try {
            // Security check: ensure the authenticated user is the same as the requested user
            // or has admin role
            String authUserId = authentication.getName();
            if (!authUserId.equals(userId.toString()) && 
                    !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to view payments for other users", null));
            }
            
            Page<PaymentResponseDTO> payments = paymentService.getPaymentsByUserId(userId, pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error retrieving payments for user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Get payments by merchant ID
     */
    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPaymentsByMerchantId(@PathVariable Long merchantId, 
                                                 Pageable pageable, 
                                                 Authentication authentication) {
        try {
            // Security check: ensure the authenticated user is the same as the merchant
            // or has admin role
            String authUserId = authentication.getName();
            if (!authUserId.equals(merchantId.toString()) && 
                    !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to view payments for this merchant", null));
            }
            
            Page<PaymentResponseDTO> payments = paymentService.getPaymentsByMerchantId(merchantId, pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error retrieving payments for merchant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Get payments by owner ID
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPaymentsByOwnerId(@PathVariable Long ownerId, 
                                              Pageable pageable, 
                                              Authentication authentication) {
        try {
            // Security check: ensure the authenticated user is the same as the owner
            // or has admin role
            String authUserId = authentication.getName();
            if (!authUserId.equals(ownerId.toString()) && 
                    !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to view payments for this owner", null));
            }
            
            Page<PaymentResponseDTO> payments = paymentService.getPaymentsByOwnerId(ownerId, pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error retrieving payments for owner", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Process a refund
     */
    @PostMapping("/refund")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processRefund(@Valid @RequestBody RefundRequestDTO refundRequestDTO, 
                                       Authentication authentication) {
        try {
            // Get the payment to check authorization
            PaymentResponseDTO payment = paymentService.getPaymentById(refundRequestDTO.getPaymentId());
            
            // Security check: ensure the authenticated user is the merchant, owner, or admin
            String authUserId = authentication.getName();
            boolean isMerchant = authUserId.equals(payment.getMerchantId() != null ? payment.getMerchantId().toString() : "");
            boolean isOwner = authUserId.equals(payment.getOwnerId() != null ? payment.getOwnerId().toString() : "");
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isMerchant && !isOwner && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to refund this payment", null));
            }
            
            PaymentResponseDTO responseDTO = paymentService.processRefund(refundRequestDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Refund processed successfully", responseDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (PaymentException e) {
            log.error("Payment refund error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error during refund processing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }
} 