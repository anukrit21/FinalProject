package com.demoApp.admin.controller;

import com.demoApp.admin.dto.UserActivityDTO;
import com.demoApp.admin.entity.UserActivity.ActivityType;
import com.demoApp.admin.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/user-activity")
@RequiredArgsConstructor
@Slf4j
public class UserActivityController {

    private final UserActivityService userActivityService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getAllUserActivities(Pageable pageable) {
        log.info("Request to get all user activities");
        return ResponseEntity.ok(userActivityService.getAllUserActivities(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserActivityDTO> getUserActivityById(@PathVariable Long id) {
        log.info("Request to get user activity with id: {}", id);
        return ResponseEntity.ok(userActivityService.getUserActivityById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getUserActivitiesByUserId(
            @PathVariable Long userId, 
            Pageable pageable) {
        log.info("Request to get activities for user with id: {}", userId);
        return ResponseEntity.ok(userActivityService.getUserActivitiesByUserId(userId, pageable));
    }

    @GetMapping("/action/{actionType}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Page<UserActivityDTO>> getUserActivitiesByActionType(
        @PathVariable String actionType, // Change type to String
        Pageable pageable) {
    log.info("Request to get activities of type: {}", actionType);

    // Convert string to enum
    ActivityType activityType;
    try {
        activityType = ActivityType.valueOf(actionType.toUpperCase()); // Ensure case insensitivity
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build(); // Handle invalid enum values
    }

    return ResponseEntity.ok(userActivityService.getUserActivitiesByType(activityType.name(), pageable));
}


    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getUserActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        log.info("Request to get activities between {} and {}", startDate, endDate);
        return ResponseEntity.ok(userActivityService.getUserActivitiesByDateRange(startDate, endDate, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserActivityDTO> createUserActivity(
            @Valid @RequestBody UserActivityDTO userActivityDTO) {
        log.info("Request to create a new user activity log: {}", userActivityDTO.getActivityType());
        return new ResponseEntity<>(userActivityService.createUserActivity(userActivityDTO), HttpStatus.CREATED);
    }

    @PostMapping("/log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserActivityDTO> logUserActivity(
            @RequestParam Long userId,
            @RequestParam ActivityType actionType,
            @RequestParam(required = false) String details) {
        log.info("Request to log user activity: {} for user {}", actionType, userId);
        return new ResponseEntity<>(
                userActivityService.logUserActivity(userId, actionType, details),
                HttpStatus.CREATED);
    }
}