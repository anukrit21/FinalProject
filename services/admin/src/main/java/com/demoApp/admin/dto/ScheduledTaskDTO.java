package com.demoApp.admin.dto;

import com.demoApp.admin.entity.ScheduledTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskDTO {
    
    private Long id;
    
    @NotBlank(message = "Task name is required")
    private String taskName;
    
    private String description;
    
    @NotBlank(message = "Service name is required")
    private String serviceName;
    
    private String cronExpression;
    
    @NotNull(message = "Status is required")
    private ScheduledTask.Status status;
    
    private LocalDateTime scheduledTime;
    
    private LocalDateTime lastExecutionTime;
    
    private LocalDateTime nextExecutionTime;
    
    private String lastExecutionResult;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Static factory method to create a DTO from an entity
    public static ScheduledTaskDTO fromEntity(ScheduledTask entity) {
        ScheduledTaskDTO dto = new ScheduledTaskDTO();
        dto.setId(entity.getId());
        dto.setTaskName(entity.getTaskName());
        dto.setDescription(entity.getDescription());
        dto.setServiceName(entity.getServiceName());
        dto.setCronExpression(entity.getCronExpression());
        dto.setStatus(entity.getStatus());
        dto.setScheduledTime(entity.getScheduledTime());
        dto.setLastExecutionTime(entity.getLastExecutionTime());
        dto.setNextExecutionTime(entity.getNextExecutionTime());
        dto.setLastExecutionResult(entity.getLastExecutionResult());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
} 