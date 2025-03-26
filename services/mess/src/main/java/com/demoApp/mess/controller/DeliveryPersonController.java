package com.demoApp.mess.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.demoApp.mess.dto.DeliveryPersonDTO;
import com.demoApp.mess.service.DeliveryPersonService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery-persons")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;
    /**
     * Get all delivery persons (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryPersonDTO>> getAllDeliveryPersons() {
        return ResponseEntity.ok(deliveryPersonService.getAllDeliveryPersons());
    }

    /**
     * Get delivery persons with pagination and search
     */
    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<Map<String, Object>> getDeliveryPersonsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(deliveryPersonService.getDeliveryPersonsPaged(page, size, search));
    }

    /**
     * Get delivery persons for current mess
     */
    @GetMapping("/mess")
    @PreAuthorize("hasRole('MESS')")
    public ResponseEntity<List<DeliveryPersonDTO>> getDeliveryPersonsForCurrentMess() {
        return ResponseEntity.ok(deliveryPersonService.getDeliveryPersonsByMess(null));
    }

    /**
     * Get delivery persons for a specific mess
     */
    @GetMapping("/mess/{messId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<List<DeliveryPersonDTO>> getDeliveryPersonsForMess(@PathVariable Long messId) {
        return ResponseEntity.ok(deliveryPersonService.getDeliveryPersonsByMess(messId));
    }

    /**
     * Get active delivery persons for a mess
     */
    @GetMapping("/mess/{messId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<List<DeliveryPersonDTO>> getActiveDeliveryPersonsForMess(@PathVariable Long messId) {
        return ResponseEntity.ok(deliveryPersonService.getActiveDeliveryPersonsByMess(messId));
    }

    /**
     * Get active delivery persons for current mess
     */
    @GetMapping("/mess/active")
    @PreAuthorize("hasRole('MESS')")
    public ResponseEntity<List<DeliveryPersonDTO>> getActiveDeliveryPersonsForCurrentMess() {
        return ResponseEntity.ok(deliveryPersonService.getActiveDeliveryPersonsByMess(null));
    }

    /**
     * Get delivery person by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryPersonDTO> getDeliveryPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPersonService.getDeliveryPersonById(id));
    }

    /**
     * Create a new delivery person
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryPersonDTO> createDeliveryPerson(@RequestBody DeliveryPersonDTO deliveryPersonDTO) {
        return new ResponseEntity<>(deliveryPersonService.createDeliveryPerson(deliveryPersonDTO), HttpStatus.CREATED);
    }

    /**
     * Update an existing delivery person
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryPersonDTO> updateDeliveryPerson(
            @PathVariable Long id,
            @RequestBody DeliveryPersonDTO deliveryPersonDTO) {
        return ResponseEntity.ok(deliveryPersonService.updateDeliveryPerson(id, deliveryPersonDTO));
    }

    /**
     * Delete a delivery person
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<Void> deleteDeliveryPerson(@PathVariable Long id) {
        deliveryPersonService.deleteDeliveryPerson(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload profile image for delivery person
     */
    @PostMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryPersonDTO> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(deliveryPersonService.uploadProfileImage(id, file));
    }

    /**
     * Upload ID proof for delivery person
     */
    @PostMapping("/{id}/id-proof")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<Object> uploadIdProof(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(deliveryPersonService.uploadIdProof(id, file));
    }

    /**
     * Activate a delivery person
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryPersonDTO> activateDeliveryPerson(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPersonService.activateDeliveryPerson(id));
    }

    /**
     * Deactivate a delivery person
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESS')")
    public ResponseEntity<DeliveryPersonDTO> deactivateDeliveryPerson(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPersonService.deactivateDeliveryPerson(id));
    }

    /**
     * Update delivery person rating
     */
    @PutMapping("/{id}/rating")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Object> updateDeliveryPersonRating(
            @PathVariable Long id,
            @RequestParam Double rating) {
        return ResponseEntity.ok(deliveryPersonService.updateDeliveryPersonRating(id, rating));
    }
} 