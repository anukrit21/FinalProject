package com.demoApp.delivery.service;

import com.demoApp.delivery.dto.PickupPointDTO;
import com.demoApp.delivery.entity.PickupPoint;
import com.demoApp.delivery.repository.PickupPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickupPointService {
    private final PickupPointRepository pickupPointRepository;
    private final ModelMapper modelMapper;
    
    @Transactional
    public PickupPointDTO createPickupPoint(PickupPointDTO pickupPointDTO) {
        PickupPoint pickupPoint = modelMapper.map(pickupPointDTO, PickupPoint.class);
        
        // Set default values if not provided
        if (pickupPoint.getLunchDeliveryStart() == null) {
            pickupPoint.setLunchDeliveryStart(LocalTime.of(13, 0)); // 1:00 PM
        }
        
        if (pickupPoint.getLunchDeliveryEnd() == null) {
            pickupPoint.setLunchDeliveryEnd(LocalTime.of(14, 0)); // 2:00 PM
        }
        
        if (pickupPoint.getDinnerDeliveryStart() == null) {
            pickupPoint.setDinnerDeliveryStart(LocalTime.of(20, 0)); // 8:00 PM
        }
        
        if (pickupPoint.getDinnerDeliveryEnd() == null) {
            pickupPoint.setDinnerDeliveryEnd(LocalTime.of(21, 0)); // 9:00 PM
        }
        
        pickupPoint.setActive(true);
        
        PickupPoint savedPickupPoint = pickupPointRepository.save(pickupPoint);
        log.info("Pickup point created with ID: {}", savedPickupPoint.getId());
        
        return modelMapper.map(savedPickupPoint, PickupPointDTO.class);
    }
    
    public PickupPointDTO getPickupPointById(Long id) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup point not found with ID: " + id));
        
        return modelMapper.map(pickupPoint, PickupPointDTO.class);
    }
    
    public List<PickupPointDTO> getAllPickupPoints() {
        List<PickupPoint> pickupPoints = pickupPointRepository.findAll();
        
        return pickupPoints.stream()
                .map(pp -> modelMapper.map(pp, PickupPointDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<PickupPointDTO> getActivePickupPoints() {
        List<PickupPoint> pickupPoints = pickupPointRepository.findByIsActiveTrue();
        
        return pickupPoints.stream()
                .map(pp -> modelMapper.map(pp, PickupPointDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<PickupPointDTO> getPickupPointsByZone(PickupPoint.CampusZone campusZone) {
        List<PickupPoint> pickupPoints = pickupPointRepository.findByCampusZone(campusZone);
        
        return pickupPoints.stream()
                .map(pp -> modelMapper.map(pp, PickupPointDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<PickupPointDTO> getActivePickupPointsByZone(PickupPoint.CampusZone campusZone) {
        List<PickupPoint> pickupPoints = pickupPointRepository.findByCampusZoneAndIsActiveTrue(campusZone);
        
        return pickupPoints.stream()
                .map(pp -> modelMapper.map(pp, PickupPointDTO.class))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PickupPointDTO updatePickupPoint(Long id, PickupPointDTO pickupPointDTO) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup point not found with ID: " + id));
        
        // Update fields
        if (pickupPointDTO.getName() != null) {
            pickupPoint.setName(pickupPointDTO.getName());
        }
        
        if (pickupPointDTO.getDescription() != null) {
            pickupPoint.setDescription(pickupPointDTO.getDescription());
        }
        
        if (pickupPointDTO.getAddress() != null) {
            pickupPoint.setAddress(pickupPointDTO.getAddress());
        }
        
        if (pickupPointDTO.getLatitude() != 0) {
            pickupPoint.setLatitude(pickupPointDTO.getLatitude());
        }
        
        if (pickupPointDTO.getLongitude() != 0) {
            pickupPoint.setLongitude(pickupPointDTO.getLongitude());
        }
        
        if (pickupPointDTO.getCampusZone() != null) {
            pickupPoint.setCampusZone(pickupPointDTO.getCampusZone());
        }
        
        if (pickupPointDTO.getOpeningTime() != null) {
            pickupPoint.setOpeningTime(pickupPointDTO.getOpeningTime());
        }
        
        if (pickupPointDTO.getClosingTime() != null) {
            pickupPoint.setClosingTime(pickupPointDTO.getClosingTime());
        }
        
        if (pickupPointDTO.getLunchDeliveryStart() != null) {
            pickupPoint.setLunchDeliveryStart(pickupPointDTO.getLunchDeliveryStart());
        }
        
        if (pickupPointDTO.getLunchDeliveryEnd() != null) {
            pickupPoint.setLunchDeliveryEnd(pickupPointDTO.getLunchDeliveryEnd());
        }
        
        if (pickupPointDTO.getDinnerDeliveryStart() != null) {
            pickupPoint.setDinnerDeliveryStart(pickupPointDTO.getDinnerDeliveryStart());
        }
        
        if (pickupPointDTO.getDinnerDeliveryEnd() != null) {
            pickupPoint.setDinnerDeliveryEnd(pickupPointDTO.getDinnerDeliveryEnd());
        }
        
        pickupPoint.setActive(pickupPointDTO.isActive());
        
        PickupPoint updatedPickupPoint = pickupPointRepository.save(pickupPoint);
        log.info("Pickup point updated with ID: {}", updatedPickupPoint.getId());
        
        return modelMapper.map(updatedPickupPoint, PickupPointDTO.class);
    }
    
    @Transactional
    public PickupPointDTO updatePickupPointStatus(Long id, boolean isActive) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup point not found with ID: " + id));
        
        pickupPoint.setActive(isActive);
        
        PickupPoint updatedPickupPoint = pickupPointRepository.save(pickupPoint);
        log.info("Pickup point status updated. ID: {}, Active: {}", id, isActive);
        
        return modelMapper.map(updatedPickupPoint, PickupPointDTO.class);
    }
    
    @Transactional
    public void deletePickupPoint(Long id) {
        if (!pickupPointRepository.existsById(id)) {
            throw new RuntimeException("Pickup point not found with ID: " + id);
        }
        
        pickupPointRepository.deleteById(id);
        log.info("Pickup point deleted with ID: {}", id);
    }
} 