package com.demoApp.admin.controller;

import com.demoApp.admin.dto.ApiResponse;
import com.demoApp.admin.dto.PaymentDTO;
import com.demoApp.admin.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Controller for viewing payment information by admin
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Get all payments with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDTO>> getAllPayments(Pageable pageable) {
        log.info("Request to get all payments");
        return ResponseEntity.ok(paymentService.findAll(pageable));
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        log.info("Request to get payment with id: {}", id);
        return ResponseEntity.ok(paymentService.findById(id));
    }

    /**
     * Get payments by user ID
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByUserId(
            @PathVariable Long userId, Pageable pageable) {
        log.info("Request to get payments for user with id: {}", userId);
        return ResponseEntity.ok(paymentService.findByUserId(userId, pageable));
    }

    /**
     * Get payments by order ID
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByOrderId(
            @PathVariable Long orderId, Pageable pageable) {
        log.info("Request to get payments for order with id: {}", orderId);
        return ResponseEntity.ok(paymentService.findByOrderId(orderId, pageable));
    }

    /**
     * Get payments by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByStatus(
            @PathVariable String status, Pageable pageable) {
        log.info("Request to get payments with status: {}", status);
        return ResponseEntity.ok(paymentService.findByStatus(status, pageable));
    }

    /**
     * Get payments within a date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        log.info("Request to get payments between {} and {}", startDate, endDate);
        return ResponseEntity.ok(paymentService.findByDateRange(startDate, endDate, pageable));
    }

    /**
     * Get payments by amount range
     */
    @GetMapping("/amount-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDTO>> getPaymentsByAmountRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount,
            Pageable pageable) {
        log.info("Request to get payments with amount between {} and {}", minAmount, maxAmount);
        return ResponseEntity.ok(paymentService.findByAmountRange(minAmount, maxAmount, pageable));
    }

    /**
     * Get payment statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPaymentStatistics() {
        log.info("Request to get payment statistics");
        return ResponseEntity.ok(new ApiResponse(true, "Payment statistics retrieved", 
                paymentService.getPaymentStatistics()));
    }

    /**
     * Get payment summary by time period
     */
    @GetMapping("/summary/{period}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPaymentSummaryByPeriod(
            @PathVariable String period) {
        log.info("Request to get payment summary for period: {}", period);
        return ResponseEntity.ok(new ApiResponse(true, "Payment summary retrieved", 
                paymentService.getPaymentSummaryByPeriod(period)));
    }
} 