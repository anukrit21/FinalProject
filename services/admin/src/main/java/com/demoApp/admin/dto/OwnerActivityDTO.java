package com.demoApp.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for representing owner activities
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerActivityDTO {

    private Long id;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    private String ownerName;

    @NotNull(message = "Action type is required")
    private String actionType;

    private String description;

    private String ipAddress;

    private String resourceType;
    
    private Long resourceId;

    private LocalDateTime timestamp;
    
    private String metadata;
} 