package com.demoApp.admin.dto;

import com.demoApp.admin.entity.UserActivity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String username;
    
    @NotNull(message = "Activity type is required")
    private UserActivity.ActivityType activityType;
    
    private String description;
    
    private String ipAddress;
    
    private String userAgent;
    
    private LocalDateTime timestamp;
    
    private String serviceName;
    
    
    public static UserActivityDTO fromEntity(UserActivity entity) {
        UserActivityDTO dto = new UserActivityDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setUsername(entity.getUsername());
        dto.setActivityType(entity.getActivityType());
        dto.setDescription(entity.getDescription());
        dto.setIpAddress(entity.getIpAddress());
        dto.setUserAgent(entity.getUserAgent());
        dto.setTimestamp(entity.getTimestamp());
        dto.setServiceName(entity.getServiceName());
        return dto;
    }
} 