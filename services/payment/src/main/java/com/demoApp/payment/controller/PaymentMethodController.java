package com.demoApp.payment.controller;

import com.demoApp.payment.dto.ApiResponse;
import com.demoApp.payment.dto.PaymentMethodDTO;
import com.demoApp.payment.entity.PaymentMethod;
import com.demoApp.payment.exception.PaymentException;
import com.demoApp.payment.exception.ResourceNotFoundException;
import com.demoApp.payment.repository.PaymentMethodRepository;
import com.demoApp.payment.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for payment method operations
 */
@RestController
@RequestMapping("/api/v1/payment-methods")
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;

    /**
     * Add a new payment method
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO,
                                          Authentication authentication) {
        try {
            // Security check
            String userId = authentication.getName();
            if (!userId.equals(paymentMethodDTO.getUserId().toString())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to add payment methods for other users", null));
            }

            PaymentMethodDTO savedMethod = paymentMethodService.savePaymentMethod(paymentMethodDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Payment method added successfully", savedMethod));
        } catch (PaymentException e) {
            log.error("Error adding payment method", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error adding payment method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Get user's payment methods
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserPaymentMethods(@PathVariable Long userId, Authentication authentication) {
        try {
            // Security check
            String authUserId = authentication.getName();
            if (!authUserId.equals(userId.toString()) &&
                    !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to view payment methods for other users", null));
            }

            List<PaymentMethod> methods = paymentMethodRepository.findByUserIdAndIsActiveTrue(userId);
            List<PaymentMethodDTO> methodDTOs = methods.stream()
                    .map(method -> modelMapper.map(method, PaymentMethodDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(true, "Payment methods retrieved successfully", methodDTOs));
        } catch (Exception e) {
            log.error("Error retrieving payment methods", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Set default payment method
     */
    @PutMapping("/{id}/default")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> setDefaultPaymentMethod(@PathVariable Long id, Authentication authentication) {
        try {
            PaymentMethod method = paymentMethodRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + id));

            // Security check
            String authUserId = authentication.getName();
            if (!authUserId.equals(method.getUserId().toString())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to modify this payment method", null));
            }

            PaymentMethodDTO updatedMethod = paymentMethodService.setDefaultPaymentMethod(id);
            return ResponseEntity.ok(new ApiResponse(true, "Default payment method updated successfully", updatedMethod));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error setting default payment method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Delete a payment method
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long id, Authentication authentication) {
        try {
            PaymentMethod method = paymentMethodRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + id));

            // Security check
            String authUserId = authentication.getName();
            if (!authUserId.equals(method.getUserId().toString()) &&
                    !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "You are not authorized to delete this payment method", null));
            }

            paymentMethodService.deletePaymentMethod(id);
            return ResponseEntity.ok(new ApiResponse(true, "Payment method deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error deleting payment method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }
} 