package com.demoApp.admin.controller;

import com.demoApp.admin.dto.ApiResponse;
import com.demoApp.admin.dto.DeliveryDTO;
import com.demoApp.admin.service.DeliveryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller for viewing delivery information by admin
 */
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * Get all deliveries with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getAllDeliveries(Pageable pageable) {
        log.info("Request to get all deliveries");
        return ResponseEntity.ok(deliveryService.findAll(pageable));
    }

    /**
     * Get delivery by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryDTO> getDeliveryById(@PathVariable Long id) {
        log.info("Request to get delivery with id: {}", id);
        return ResponseEntity.ok(deliveryService.findById(id));
    }

    /**
     * Get deliveries by order ID
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getDeliveriesByOrderId(
            @PathVariable Long orderId, Pageable pageable) {
        log.info("Request to get deliveries for order with id: {}", orderId);
        return ResponseEntity.ok(deliveryService.findByOrderId(orderId, pageable));
    }

    /**
     * Get deliveries by user ID
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getDeliveriesByUserId(
            @PathVariable Long userId, Pageable pageable) {
        log.info("Request to get deliveries for user with id: {}", userId);
        return ResponseEntity.ok(deliveryService.findByUserId(userId, pageable));
    }

    /**
     * Get deliveries by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getDeliveriesByStatus(
            @PathVariable String status, Pageable pageable) {
        log.info("Request to get deliveries with status: {}", status);
        return ResponseEntity.ok(deliveryService.findByStatus(status, pageable));
    }

    /**
     * Get deliveries within a date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getDeliveriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        log.info("Request to get deliveries between {} and {}", startDate, endDate);
        return ResponseEntity.ok(deliveryService.findByDateRange(startDate, endDate, pageable));
    }

    /**
     * Get deliveries by courier
     */
    @GetMapping("/courier/{courierId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getDeliveriesByCourier(
            @PathVariable Long courierId, Pageable pageable) {
        log.info("Request to get deliveries for courier with id: {}", courierId);
        return ResponseEntity.ok(deliveryService.findByCourierId(courierId, pageable));
    }

    /**
     * Get delivery statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getDeliveryStatistics() {
        log.info("Request to get delivery statistics");
        return ResponseEntity.ok(new ApiResponse(true, "Delivery statistics retrieved", 
                deliveryService.getDeliveryStatistics()));
    }

    /**
     * Get pending deliveries
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getPendingDeliveries(Pageable pageable) {
        log.info("Request to get pending deliveries");
        return ResponseEntity.ok(deliveryService.findPendingDeliveries(pageable));
    }

    /**
     * Get delayed deliveries
     */
    @GetMapping("/delayed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DeliveryDTO>> getDelayedDeliveries(Pageable pageable) {
        log.info("Request to get delayed deliveries");
        return ResponseEntity.ok(deliveryService.findDelayedDeliveries(pageable));
    }
} 