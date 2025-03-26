package com.demoApp.admin.controller;

import com.demoApp.admin.dto.ApiResponse;
import com.demoApp.admin.dto.OwnerActivityDTO;
import com.demoApp.admin.service.OwnerActivityService;

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
 * Controller for managing and viewing owner activities
 */
@RestController
@RequestMapping("/api/v1/owner-activity")
@RequiredArgsConstructor
@Slf4j
public class OwnerActivityController {

    private final OwnerActivityService ownerActivityService;

    /**
     * Get all owner activities with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OwnerActivityDTO>> getAllOwnerActivities(Pageable pageable) {
        log.info("Request to get all owner activities");
        return ResponseEntity.ok(ownerActivityService.findAll(pageable));
    }

    /**
     * Get owner activity by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OwnerActivityDTO> getOwnerActivityById(@PathVariable Long id) {
        log.info("Request to get owner activity with id: {}", id);
        return ResponseEntity.ok(ownerActivityService.findById(id));
    }

    /**
     * Get owner activities by owner ID
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OwnerActivityDTO>> getOwnerActivitiesByOwnerId(
            @PathVariable Long ownerId, Pageable pageable) {
        log.info("Request to get activities for owner with id: {}", ownerId);
        return ResponseEntity.ok(ownerActivityService.findByOwnerId(ownerId, pageable));
    }

    /**
     * Get owner activities by action type
     */
    @GetMapping("/action/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OwnerActivityDTO>> getOwnerActivitiesByActionType(
            @PathVariable String actionType, Pageable pageable) {
        log.info("Request to get owner activities of type: {}", actionType);
        return ResponseEntity.ok(ownerActivityService.findByActionType(actionType, pageable));
    }

    /**
     * Get owner activities within a date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OwnerActivityDTO>> getOwnerActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        log.info("Request to get owner activities between {} and {}", startDate, endDate);
        return ResponseEntity.ok(ownerActivityService.findByDateRange(startDate, endDate, pageable));
    }

    /**
     * Get summary of owner activities
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getOwnerActivitySummary() {
        log.info("Request to get owner activity summary");
        return ResponseEntity.ok(new ApiResponse(true, "Owner activity summary retrieved", 
                ownerActivityService.getActivitySummary()));
    }

    /**
     * Get most active owners
     */
    @GetMapping("/most-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getMostActiveOwners(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Request to get top {} most active owners", limit);
        return ResponseEntity.ok(new ApiResponse(true, "Most active owners retrieved", 
                ownerActivityService.getMostActiveOwners(limit)));
    }
} 