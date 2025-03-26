package com.demoApp.payment.controller;

import com.demoApp.payment.dto.ApiResponse;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin controller for payment operations
 */
@RestController
@RequestMapping("/api/v1/payments/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {

    private final PaymentService paymentService;

    /**
     * Get all payments (admin only)
     */
    @GetMapping
    public ResponseEntity<?> getAllPayments(Pageable pageable) {
        try {
            Page<PaymentResponseDTO> payments = paymentService.getAllPayments(pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Payments retrieved successfully", payments));
        } catch (Exception e) {
            log.error("Error retrieving all payments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Force refund a payment (admin only)
     */
    @PostMapping("/refund")
    public ResponseEntity<?> adminRefund(@Valid @RequestBody RefundRequestDTO refundRequestDTO,
                                     Authentication authentication) {
        try {
            // Set admin ID in request
            Long adminId = Long.valueOf(authentication.getName());
            refundRequestDTO.setAdminId(adminId);
            
            PaymentResponseDTO responseDTO = paymentService.processRefund(refundRequestDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Admin refund processed successfully", responseDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (PaymentException e) {
            log.error("Admin refund error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error during admin refund", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }

    /**
     * Get payment statistics (admin only)
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getPaymentStats(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            // If dates are not provided, use last 30 days
            if (startDate == null) {
                startDate = LocalDateTime.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDateTime.now();
            }
            
            // Collect statistics (this would be more sophisticated in a real application)
            Map<String, Object> stats = new HashMap<>();
            
            // Calculate statistics
            // This is just a placeholder - in a real application, you would have more detailed stats
            BigDecimal totalProcessed = paymentService.calculateTotalProcessedAmount(startDate, endDate);
            // Add more statistics as needed
            
            stats.put("totalProcessed", totalProcessed);
            stats.put("startDate", startDate);
            stats.put("endDate", endDate);
            
            return ResponseEntity.ok(new ApiResponse(true, "Payment statistics retrieved successfully", stats));
        } catch (Exception e) {
            log.error("Error retrieving payment statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred", null));
        }
    }
} 