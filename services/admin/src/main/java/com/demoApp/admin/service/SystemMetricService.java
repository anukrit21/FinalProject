package com.demoApp.admin.service;

import com.demoApp.admin.dto.SystemMetricDTO;
import com.demoApp.admin.entity.SystemMetric;
import com.demoApp.admin.exception.ResourceNotFoundException;
import com.demoApp.admin.repository.SystemMetricRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemMetricService {
    
    private final SystemMetricRepository systemMetricRepository;
    private final ModelMapper modelMapper;
    
    public List<SystemMetric> getAllSystemMetrics(Pageable pageable) {
        return systemMetricRepository.findAll(pageable).getContent();
    }
    
    public SystemMetric getSystemMetricById(Long id) {
        return systemMetricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System metric not found with id: " + id));
    }
    
    public Page<SystemMetric> getSystemMetricsByService(String serviceName, Pageable pageable) {
        return systemMetricRepository.findByServiceNameOrderByTimestampDesc(serviceName, pageable);
    }
    
    public Page<SystemMetric> getSystemMetricsByType(SystemMetric.MetricType metricType, Pageable pageable) {
        return systemMetricRepository.findByMetricTypeOrderByTimestampDesc(metricType, pageable);
    }
    
    public Page<SystemMetric> getSystemMetricsByServiceAndType(
            String serviceName, SystemMetric.MetricType metricType, Pageable pageable) {
        return systemMetricRepository.findByServiceNameAndMetricTypeOrderByTimestampDesc(
                serviceName, metricType, pageable);
    }
    
    public Page<SystemMetric> getSystemMetricsByDateRange(
            LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return systemMetricRepository.findByTimestampBetweenOrderByTimestampDesc(start, end, pageable);
    }
    
    public SystemMetric getLatestMetricByServiceAndType(String serviceName, SystemMetric.MetricType metricType) {
        SystemMetric metric = systemMetricRepository.findLatestMetricByServiceAndType(serviceName, metricType);
        if (metric == null) {
            throw new ResourceNotFoundException(
                    "No metrics found for service: " + serviceName + " and type: " + metricType);
        }
        return metric;
    }
    
    @Transactional
    public SystemMetric createSystemMetric(SystemMetricDTO systemMetricDTO) {
        SystemMetric systemMetric = modelMapper.map(systemMetricDTO, SystemMetric.class);
        systemMetric.setTimestamp(LocalDateTime.now());
        return systemMetricRepository.save(systemMetric);
    }
    
    @Transactional
    public SystemMetric recordMetric(String serviceName, SystemMetric.MetricType metricType, 
                                Double numericValue, String stringValue, String notes) {
        SystemMetric systemMetric = new SystemMetric();
        systemMetric.setServiceName(serviceName);
        systemMetric.setMetricType(metricType);
        systemMetric.setNumericValue(numericValue);
        systemMetric.setStringValue(stringValue);
        systemMetric.setNotes(notes);
        systemMetric.setTimestamp(LocalDateTime.now());
        
        return systemMetricRepository.save(systemMetric);
    }
    
    public Map<String, Double> getLatestMetricsForService(String serviceName) {
        Map<String, Double> metrics = new HashMap<>();
        
        for (SystemMetric.MetricType metricType : SystemMetric.MetricType.values()) {
            try {
                SystemMetric metric = getLatestMetricByServiceAndType(serviceName, metricType);
                if (metric.getNumericValue() != null) {
                    metrics.put(metricType.name(), metric.getNumericValue());
                }
            } catch (ResourceNotFoundException e) {
                // Ignore if no metrics found
            }
        }
        
        return metrics;
    }
    
    public SystemMetricDTO convertToDTO(SystemMetric systemMetric) {
        return SystemMetricDTO.fromEntity(systemMetric);
    }
    
    public List<SystemMetricDTO> convertToDTOs(List<SystemMetric> systemMetrics) {
        return systemMetrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 