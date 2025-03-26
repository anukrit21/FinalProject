package com.demoApp.mess.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.demoApp.mess.dto.DeliveryDTO;
import com.demoApp.mess.enums.DeliveryStatus;
import com.demoApp.mess.service.DeliveryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/deliveries")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryDTO>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    /**
     * Get deliveries with pagination and filtering
     */
    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<Map<String, Object>> getDeliveriesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesPaged(page, size, status));
    }

    /**
     * Get deliveries for current user (based on role)
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesForCurrentUser(page, size, status));
    }

    /**
     * Get pending deliveries for assignment
     */
    @GetMapping("/pending-assignment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<List<DeliveryDTO>> getPendingDeliveriesForAssignment(
            @RequestParam(required = false) Long messId) {
        return ResponseEntity.ok(deliveryService.getPendingDeliveriesForAssignment(messId));
    }

    /**
     * Get delivery by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDTO> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    /**
     * Get delivery by tracking code (public access)
     */
    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<DeliveryDTO> trackDelivery(@PathVariable String trackingCode) {
        return ResponseEntity.ok(deliveryService.getDeliveryByTrackingCode(trackingCode));
    }

    /**
     * Create a new delivery
     */
    @PostMapping
    public ResponseEntity<DeliveryDTO> createDelivery(@RequestBody DeliveryDTO deliveryDTO) {
        return new ResponseEntity<>(deliveryService.createDelivery(deliveryDTO), HttpStatus.CREATED);
    }

    /**
     * Assign delivery person to a delivery
     */
    @PutMapping("/{deliveryId}/assign/{deliveryPersonId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryDTO> assignDeliveryPerson(
            @PathVariable Long deliveryId,
            @PathVariable Long deliveryPersonId) {
        return ResponseEntity.ok(deliveryService.assignDeliveryPerson(deliveryId, deliveryPersonId));
    }

    /**
     * Update delivery status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryDTO> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, status));
    }

    /**
     * Update delivery location (for mobile app)
     */
    @PutMapping("/{id}/location")
    public ResponseEntity<DeliveryDTO> updateDeliveryLocation(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        return ResponseEntity.ok(deliveryService.updateDeliveryLocation(id, latitude, longitude));
    }

    /**
     * Add delivery feedback and rating (customer only)
     */
    @PostMapping("/{id}/feedback")
    public ResponseEntity<DeliveryDTO> addFeedbackAndRating(
            @PathVariable Long id,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(deliveryService.addFeedbackAndRating(id, rating, feedback));
    }

    /**
     * Report delivery issue
     */
    @PostMapping("/{id}/issue")
    public ResponseEntity<DeliveryDTO> reportIssue(
            @PathVariable Long id,
            @RequestParam String issueDescription) {
        return ResponseEntity.ok(deliveryService.reportIssue(id, issueDescription));
    }

    /**
     * Resolve delivery issue (admin or mess only)
     */
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryDTO> resolveIssue(
            @PathVariable Long id,
            @RequestParam String resolution) {
        return ResponseEntity.ok(deliveryService.resolveIssue(id, resolution));
    }

    /**
     * Cancel delivery
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<DeliveryDTO> cancelDelivery(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(deliveryService.cancelDelivery(id, reason));
    }

    /**
     * Get delivery statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<Map<String, Object>> getDeliveryStatistics(
            @RequestParam(required = false) Long messId) {
        return ResponseEntity.ok(deliveryService.getDeliveryStatistics(messId));
    }
} 