package com.demoApp.admin.dto;

import com.demoApp.admin.entity.ServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatusDTO {
    
    private Long id;
    
    @NotBlank(message = "Service name is required")
    private String serviceName;
    
    @NotBlank(message = "Service URL is required")
    private String serviceUrl;
    
    @NotNull(message = "Status is required")
    private ServiceStatus.Status status;
    
    private String version;
    
    private LocalDateTime lastChecked;
    
    private String healthDetails;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 