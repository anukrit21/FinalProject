package com.demoApp.admin.controller;

import com.demoApp.admin.dto.SystemMetricDTO;
import com.demoApp.admin.entity.SystemMetric;
import com.demoApp.admin.service.SystemMetricService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system-metrics")
@RequiredArgsConstructor
public class SystemMetricController {
    
    private final SystemMetricService systemMetricService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemMetricDTO>> getAllSystemMetrics(Pageable pageable) {
        List<SystemMetric> metrics = systemMetricService.getAllSystemMetrics(pageable);
        return ResponseEntity.ok(systemMetricService.convertToDTOs(metrics));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemMetricDTO> getSystemMetricById(@PathVariable Long id) {
        SystemMetric metric = systemMetricService.getSystemMetricById(id);
        return ResponseEntity.ok(systemMetricService.convertToDTO(metric));
    }
    
    @GetMapping("/service/{serviceName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemMetricDTO>> getSystemMetricsByService(
            @PathVariable String serviceName, Pageable pageable) {
        Page<SystemMetric> metrics = systemMetricService.getSystemMetricsByService(serviceName, pageable);
        return ResponseEntity.ok(metrics.map(systemMetricService::convertToDTO));
    }
    
    @GetMapping("/type/{metricType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemMetricDTO>> getSystemMetricsByType(
            @PathVariable SystemMetric.MetricType metricType, Pageable pageable) {
        Page<SystemMetric> metrics = systemMetricService.getSystemMetricsByType(metricType, pageable);
        return ResponseEntity.ok(metrics.map(systemMetricService::convertToDTO));
    }
    
    @GetMapping("/service/{serviceName}/type/{metricType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemMetricDTO>> getSystemMetricsByServiceAndType(
            @PathVariable String serviceName,
            @PathVariable SystemMetric.MetricType metricType,
            Pageable pageable) {
        Page<SystemMetric> metrics = systemMetricService.getSystemMetricsByServiceAndType(
                serviceName, metricType, pageable);
        return ResponseEntity.ok(metrics.map(systemMetricService::convertToDTO));
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SystemMetricDTO>> getSystemMetricsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable) {
        Page<SystemMetric> metrics = systemMetricService.getSystemMetricsByDateRange(start, end, pageable);
        return ResponseEntity.ok(metrics.map(systemMetricService::convertToDTO));
    }
    
    @GetMapping("/latest/{serviceName}/{metricType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemMetricDTO> getLatestMetricByServiceAndType(
            @PathVariable String serviceName,
            @PathVariable SystemMetric.MetricType metricType) {
        SystemMetric metric = systemMetricService.getLatestMetricByServiceAndType(serviceName, metricType);
        return ResponseEntity.ok(systemMetricService.convertToDTO(metric));
    }
    
    @GetMapping("/latest/{serviceName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Double>> getLatestMetricsForService(@PathVariable String serviceName) {
        return ResponseEntity.ok(systemMetricService.getLatestMetricsForService(serviceName));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemMetricDTO> createSystemMetric(@Valid @RequestBody SystemMetricDTO systemMetricDTO) {
        SystemMetric metric = systemMetricService.createSystemMetric(systemMetricDTO);
        return new ResponseEntity<>(systemMetricService.convertToDTO(metric), HttpStatus.CREATED);
    }
    
    @PostMapping("/record")
    public ResponseEntity<SystemMetricDTO> recordMetric(
            @RequestParam String serviceName,
            @RequestParam SystemMetric.MetricType metricType,
            @RequestParam(required = false) Double numericValue,
            @RequestParam(required = false) String stringValue,
            @RequestParam(required = false) String notes) {
        
        SystemMetric metric = systemMetricService.recordMetric(
                serviceName, metricType, numericValue, stringValue, notes);
        
        return ResponseEntity.ok(systemMetricService.convertToDTO(metric));
    }
} 