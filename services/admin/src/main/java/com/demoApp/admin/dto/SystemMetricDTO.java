package com.demoApp.admin.dto;

import com.demoApp.admin.entity.SystemMetric;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetricDTO {
    
    private Long id;
    
    @NotBlank(message = "Service name is required")
    private String serviceName;
    
    @NotNull(message = "Metric type is required")
    private SystemMetric.MetricType metricType;
    
    private Double numericValue;
    
    private String stringValue;
    
    private LocalDateTime timestamp;
    
    private String notes;
    
    // Static factory method to create a DTO from an entity
    public static SystemMetricDTO fromEntity(SystemMetric entity) {
        SystemMetricDTO dto = new SystemMetricDTO();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setMetricType(entity.getMetricType());
        dto.setNumericValue(entity.getNumericValue());
        dto.setStringValue(entity.getStringValue());
        dto.setTimestamp(entity.getTimestamp());
        dto.setNotes(entity.getNotes());
        return dto;
    }
} 