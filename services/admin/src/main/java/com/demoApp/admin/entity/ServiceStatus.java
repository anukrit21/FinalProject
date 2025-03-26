package com.demoApp.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus {

    public enum Status {
        UP, DOWN, DEGRADED, MAINTENANCE, UNKNOWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serviceName;

    @Column(nullable = false)
    private String serviceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.UNKNOWN;

    private String version;

    private LocalDateTime lastChecked;

    private String healthDetails;
    
    @Builder.Default
    private Integer responseTimeMs = 0;
    
    private String errorMessage;
    
    @Builder.Default
    private Integer consecutiveFailures = 0;
    
    @Builder.Default
    private boolean alertSent = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}