package com.demoApp.admin.service;

import com.demoApp.admin.dto.ServiceStatusDTO;
import com.demoApp.admin.entity.ServiceStatus;
import com.demoApp.admin.exception.ResourceNotFoundException;
import com.demoApp.admin.repository.ServiceStatusRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceStatusService {
    
    private final ServiceStatusRepository serviceStatusRepository;
    private final ModelMapper modelMapper;
    private final WebClient.Builder webClientBuilder;
    
    public List<ServiceStatus> getAllServiceStatuses() {
        return serviceStatusRepository.findAll();
    }
    
    public ServiceStatus getServiceStatusById(Long id) {
        return serviceStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service status not found with id: " + id));
    }
    
    public ServiceStatus getServiceStatusByName(String serviceName) {
        return serviceStatusRepository.findByServiceName(serviceName)
                .orElseThrow(() -> new ResourceNotFoundException("Service status not found with name: " + serviceName));
    }
    
    @Transactional
    public ServiceStatus createServiceStatus(ServiceStatusDTO serviceStatusDTO) {
        ServiceStatus serviceStatus = modelMapper.map(serviceStatusDTO, ServiceStatus.class);
        serviceStatus.setLastChecked(LocalDateTime.now());
        return serviceStatusRepository.save(serviceStatus);
    }
    
    @Transactional
    public ServiceStatus updateServiceStatus(Long id, ServiceStatusDTO serviceStatusDTO) {
        ServiceStatus existingServiceStatus = getServiceStatusById(id);
        
        if (serviceStatusDTO.getServiceName() != null) {
            existingServiceStatus.setServiceName(serviceStatusDTO.getServiceName());
        }
        if (serviceStatusDTO.getServiceUrl() != null) {
            existingServiceStatus.setServiceUrl(serviceStatusDTO.getServiceUrl());
        }
        if (serviceStatusDTO.getStatus() != null) {
            existingServiceStatus.setStatus(serviceStatusDTO.getStatus());
        }
        if (serviceStatusDTO.getVersion() != null) {
            existingServiceStatus.setVersion(serviceStatusDTO.getVersion());
        }
        if (serviceStatusDTO.getHealthDetails() != null) {
            existingServiceStatus.setHealthDetails(serviceStatusDTO.getHealthDetails());
        }
        
        existingServiceStatus.setLastChecked(LocalDateTime.now());
        return serviceStatusRepository.save(existingServiceStatus);
    }
    
    @Transactional
    public void deleteServiceStatus(Long id) {
        ServiceStatus serviceStatus = getServiceStatusById(id);
        serviceStatusRepository.delete(serviceStatus);
    }
    
    @Scheduled(fixedRate = 60000) // Check every minute
    @Transactional
    public void checkAllServicesHealth() {
        List<ServiceStatus> services = serviceStatusRepository.findAll();
        
        for (ServiceStatus service : services) {
            try {
                // Try to call the service's health endpoint
                String healthEndpoint = service.getServiceUrl() + "/actuator/health";
                
                webClientBuilder.build()
                    .get()
                    .uri(healthEndpoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                        response -> {
                            service.setStatus(ServiceStatus.Status.UP);
                            service.setHealthDetails(response);
                            service.setLastChecked(LocalDateTime.now());
                            serviceStatusRepository.save(service);
                        },
                        error -> {
                            service.setStatus(ServiceStatus.Status.DOWN);
                            service.setHealthDetails("Error: " + error.getMessage());
                            service.setLastChecked(LocalDateTime.now());
                            serviceStatusRepository.save(service);
                        }
                    );
                
            } catch (Exception e) {
                service.setStatus(ServiceStatus.Status.DOWN);
                service.setHealthDetails("Error: " + e.getMessage());
                service.setLastChecked(LocalDateTime.now());
                serviceStatusRepository.save(service);
            }
        }
    }
    
    public ServiceStatusDTO convertToDTO(ServiceStatus serviceStatus) {
        return modelMapper.map(serviceStatus, ServiceStatusDTO.class);
    }
    
    public List<ServiceStatusDTO> convertToDTOs(List<ServiceStatus> serviceStatuses) {
        return serviceStatuses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 