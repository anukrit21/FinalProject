package com.demoApp.delivery.service;

import com.demoApp.delivery.dto.DeliveryPersonDTO;
import com.demoApp.delivery.entity.DeliveryPerson;
import com.demoApp.delivery.repository.DeliveryPersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryPersonService {
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public DeliveryPersonDTO createDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO) {
        // Check if email already exists
        if (deliveryPersonRepository.findByEmail(deliveryPersonDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        // Check if mobile number already exists
        if (deliveryPersonRepository.findByMobileNumber(deliveryPersonDTO.getMobileNumber()).isPresent()) {
            throw new RuntimeException("Mobile number already registered");
        }
        
        DeliveryPerson deliveryPerson = modelMapper.map(deliveryPersonDTO, DeliveryPerson.class);
        
        // Encode password
        deliveryPerson.setPassword(passwordEncoder.encode(deliveryPersonDTO.getPassword()));
        
        // Set default values
        deliveryPerson.setAvailable(false);
        deliveryPerson.setVerified(false);
        deliveryPerson.setRating(0.0);
        deliveryPerson.setTotalDeliveries(0);
        deliveryPerson.setStatus(DeliveryPerson.DeliveryPersonStatus.INACTIVE);
        deliveryPerson.setCreatedAt(LocalDateTime.now());
        
        DeliveryPerson savedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person created with ID: {}", savedDeliveryPerson.getId());
        
        return modelMapper.map(savedDeliveryPerson, DeliveryPersonDTO.class);
    }
    
    public DeliveryPersonDTO getDeliveryPersonById(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + id));
        
        return modelMapper.map(deliveryPerson, DeliveryPersonDTO.class);
    }
    
    public DeliveryPersonDTO getDeliveryPersonByEmail(String email) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with email: " + email));
        
        return modelMapper.map(deliveryPerson, DeliveryPersonDTO.class);
    }
    
    public List<DeliveryPersonDTO> getAllDeliveryPersons() {
        List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findAll();
        
        return deliveryPersons.stream()
                .map(dp -> modelMapper.map(dp, DeliveryPersonDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<DeliveryPersonDTO> getAvailableDeliveryPersons() {
        List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findByIsAvailableTrue();
        
        return deliveryPersons.stream()
                .map(dp -> modelMapper.map(dp, DeliveryPersonDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<DeliveryPersonDTO> getDeliveryPersonsByZone(DeliveryPerson.DeliveryZone zone) {
        List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findByDeliveryZone(zone);
        
        return deliveryPersons.stream()
                .map(dp -> modelMapper.map(dp, DeliveryPersonDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<DeliveryPersonDTO> getAvailableDeliveryPersonsByZone(DeliveryPerson.DeliveryZone zone) {
        List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findByDeliveryZoneAndIsAvailableTrue(zone);
        
        return deliveryPersons.stream()
                .map(dp -> modelMapper.map(dp, DeliveryPersonDTO.class))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DeliveryPersonDTO updateDeliveryPerson(Long id, DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + id));
        
        // Update fields
        if (deliveryPersonDTO.getName() != null) {
            deliveryPerson.setName(deliveryPersonDTO.getName());
        }
        
        if (deliveryPersonDTO.getVehicleNumber() != null) {
            deliveryPerson.setVehicleNumber(deliveryPersonDTO.getVehicleNumber());
        }
        
        if (deliveryPersonDTO.getVehicleType() != null) {
            deliveryPerson.setVehicleType(deliveryPersonDTO.getVehicleType());
        }
        
        if (deliveryPersonDTO.getDeliveryZone() != null) {
            deliveryPerson.setDeliveryZone(deliveryPersonDTO.getDeliveryZone());
        }
        
        if (deliveryPersonDTO.getStatus() != null) {
            deliveryPerson.setStatus(deliveryPersonDTO.getStatus());
        }
        
        deliveryPerson.setAvailable(deliveryPersonDTO.isAvailable());
        deliveryPerson.setUpdatedAt(LocalDateTime.now());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person updated with ID: {}", updatedDeliveryPerson.getId());
        
        return modelMapper.map(updatedDeliveryPerson, DeliveryPersonDTO.class);
    }
    
    @Transactional
    public DeliveryPersonDTO updateAvailabilityStatus(Long id, boolean isAvailable) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + id));
        
        deliveryPerson.setAvailable(isAvailable);
        deliveryPerson.setUpdatedAt(LocalDateTime.now());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person availability updated. ID: {}, Available: {}", id, isAvailable);
        
        return modelMapper.map(updatedDeliveryPerson, DeliveryPersonDTO.class);
    }
    
    @Transactional
    public DeliveryPersonDTO updateDeliveryPersonStatus(Long id, DeliveryPerson.DeliveryPersonStatus status) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + id));
        
        deliveryPerson.setStatus(status);
        deliveryPerson.setUpdatedAt(LocalDateTime.now());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person status updated. ID: {}, Status: {}", id, status);
        
        return modelMapper.map(updatedDeliveryPerson, DeliveryPersonDTO.class);
    }
    
    @Transactional
    public void deleteDeliveryPerson(Long id) {
        if (!deliveryPersonRepository.existsById(id)) {
            throw new RuntimeException("Delivery person not found with ID: " + id);
        }
        
        deliveryPersonRepository.deleteById(id);
        log.info("Delivery person deleted with ID: {}", id);
    }
    
    @Transactional
    public void updateDeliveryPersonRating(Long id, double rating) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + id));
        
        // Calculate new average rating
        double currentRating = deliveryPerson.getRating();
        int totalDeliveries = deliveryPerson.getTotalDeliveries();
        
        double newRating;
        if (totalDeliveries == 0) {
            newRating = rating;
        } else {
            newRating = ((currentRating * totalDeliveries) + rating) / (totalDeliveries + 1);
        }
        
        deliveryPerson.setRating(newRating);
        deliveryPerson.setTotalDeliveries(totalDeliveries + 1);
        
        deliveryPersonRepository.save(deliveryPerson);
        log.info("Delivery person rating updated. ID: {}, New Rating: {}", id, newRating);
    }
} 