package com.demoApp.delivery.service;

import com.demoApp.delivery.dto.DeliveryDTO;
import com.demoApp.delivery.entity.Delivery;
import com.demoApp.delivery.entity.DeliveryPerson;
import com.demoApp.delivery.entity.PickupPoint;
import com.demoApp.delivery.repository.DeliveryRepository;
import com.demoApp.delivery.repository.DeliveryPersonRepository;
import com.demoApp.delivery.repository.PickupPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final PickupPointRepository pickupPointRepository;
    private final ModelMapper modelMapper;
    
    @Transactional
    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) {
        Delivery delivery = modelMapper.map(deliveryDTO, Delivery.class);
        
        // Validate pickup point
        if (deliveryDTO.getPickupPointId() != null) {
            PickupPoint pickupPoint = pickupPointRepository.findById(deliveryDTO.getPickupPointId())
                    .orElseThrow(() -> new RuntimeException("Pickup point not found with ID: " + deliveryDTO.getPickupPointId()));
            delivery.setPickupPoint(pickupPoint);
        }
        
        // Set initial status if not provided
        if (delivery.getStatus() == null) {
            delivery.setStatus(Delivery.DeliveryStatus.PENDING);
        }
        
        // Set creation timestamp
        delivery.setCreatedAt(LocalDateTime.now());
        
        // If delivery person assigned, validate
        if (deliveryDTO.getDeliveryPersonId() != null) {
            DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryDTO.getDeliveryPersonId())
                    .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + deliveryDTO.getDeliveryPersonId()));
            
            // Check if delivery person is available
            if (!deliveryPerson.isAvailable()) {
                throw new RuntimeException("Delivery person with ID " + deliveryDTO.getDeliveryPersonId() + " is not available");
            }
            
            delivery.setDeliveryPerson(deliveryPerson);
            deliveryPerson.setAvailable(false); // Mark as unavailable
            deliveryPersonRepository.save(deliveryPerson);
        }
        
        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery created with ID: {}", savedDelivery.getId());
        
        return modelMapper.map(savedDelivery, DeliveryDTO.class);
    }
    
    public DeliveryDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + id));
        
        return modelMapper.map(delivery, DeliveryDTO.class);
    }
    
    public List<DeliveryDTO> getAllDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAll();
        
        return deliveries.stream()
                .map(d -> modelMapper.map(d, DeliveryDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<DeliveryDTO> getDeliveriesByStatus(Delivery.DeliveryStatus status) {
        List<Delivery> deliveries = deliveryRepository.findByStatus(status);
        
        return deliveries.stream()
                .map(d -> modelMapper.map(d, DeliveryDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<DeliveryDTO> getDeliveriesByDeliveryPerson(Long deliveryPersonId) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + deliveryPersonId));
        
        List<Delivery> deliveries = deliveryRepository.findByDeliveryPerson(deliveryPerson);
        
        return deliveries.stream()
                .map(d -> modelMapper.map(d, DeliveryDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<DeliveryDTO> getDeliveriesByPickupPoint(Long pickupPointId) {
        PickupPoint pickupPoint = pickupPointRepository.findById(pickupPointId)
                .orElseThrow(() -> new RuntimeException("Pickup point not found with ID: " + pickupPointId));
        
        List<Delivery> deliveries = deliveryRepository.findByPickupPoint(pickupPoint);
        
        return deliveries.stream()
                .map(d -> modelMapper.map(d, DeliveryDTO.class))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DeliveryDTO assignDeliveryPerson(Long deliveryId, Long deliveryPersonId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with ID: " + deliveryPersonId));
        
        // Check if delivery person is available
        if (!deliveryPerson.isAvailable()) {
            throw new RuntimeException("Delivery person with ID " + deliveryPersonId + " is not available");
        }
        
        // Check if delivery is in assignable state
        if (delivery.getStatus() != Delivery.DeliveryStatus.PENDING) {
            throw new RuntimeException("Delivery with ID " + deliveryId + " is not in PENDING status and cannot be assigned");
        }
        
        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        
        deliveryPerson.setAvailable(false);
        deliveryPersonRepository.save(deliveryPerson);
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery person assigned to delivery. Delivery ID: {}, Delivery Person ID: {}", deliveryId, deliveryPersonId);
        
        return modelMapper.map(updatedDelivery, DeliveryDTO.class);
    }
    
    @Transactional
    public DeliveryDTO updateDeliveryStatus(Long deliveryId, Delivery.DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));
        
        // Validate status transition
        validateStatusTransition(delivery.getStatus(), status);
        
        delivery.setStatus(status);
        
        // Update status-specific timestamps
        updateStatusTimestamps(delivery, status);
        
        // Free up delivery person if delivery is completed or cancelled
        if (status == Delivery.DeliveryStatus.DELIVERED || status == Delivery.DeliveryStatus.CANCELLED) {
            if (delivery.getDeliveryPerson() != null) {
                DeliveryPerson deliveryPerson = delivery.getDeliveryPerson();
                deliveryPerson.setAvailable(true);
                deliveryPersonRepository.save(deliveryPerson);
            }
        }
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery status updated. Delivery ID: {}, Status: {}", deliveryId, status);
        
        return modelMapper.map(updatedDelivery, DeliveryDTO.class);
    }
    
    private void validateStatusTransition(Delivery.DeliveryStatus currentStatus, Delivery.DeliveryStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                if (newStatus != Delivery.DeliveryStatus.ASSIGNED && newStatus != Delivery.DeliveryStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case ASSIGNED:
                if (newStatus != Delivery.DeliveryStatus.PICKED_UP && newStatus != Delivery.DeliveryStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case PICKED_UP:
                if (newStatus != Delivery.DeliveryStatus.IN_TRANSIT && newStatus != Delivery.DeliveryStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case IN_TRANSIT:
                if (newStatus != Delivery.DeliveryStatus.DELIVERED && newStatus != Delivery.DeliveryStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new RuntimeException("Cannot change status from " + currentStatus);
            default:
                throw new RuntimeException("Unknown delivery status: " + currentStatus);
        }
    }
    
    private void updateStatusTimestamps(Delivery delivery, Delivery.DeliveryStatus status) {
        switch (status) {
            case ASSIGNED:
                delivery.setAssignedAt(LocalDateTime.now());
                break;
            case ACCEPTED:  
                delivery.setAcceptedTime(LocalDateTime.now());
                break;
            case PICKED_UP:
                delivery.setPickedUpTime(LocalDateTime.now()); 
                break;
            case IN_TRANSIT:
                delivery.setInTransitAt(LocalDateTime.now());
                break;
            case DELIVERED:
                delivery.setDeliveredTime(LocalDateTime.now());
                break;
            case CANCELLED:
                delivery.setCancelledAt(LocalDateTime.now());
                break;
            case FAILED: 
                break;
            case PENDING:  
                break;
        }
    } 
    
       
    @Transactional
        public DeliveryDTO updateDelivery(Long id, DeliveryDTO deliveryDTO) {
    Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + id));
    
    // Update fields that are allowed to be updated
    if (deliveryDTO.getDestinationAddress() != null) {
        delivery.setDeliveryAddress(deliveryDTO.getDestinationAddress()); // Changed to setDeliveryAddress
    }
    
    // Update latitude with proper type casting
    if (deliveryDTO.getDestinationLatitude() != 0) {
        delivery.setDeliveryLatitude(deliveryDTO.getDestinationLatitude()); // Changed to setDeliveryLatitude
    }
    
    // Update longitude with proper type casting
    if (deliveryDTO.getDestinationLongitude() != 0) {
        delivery.setDeliveryLongitude(deliveryDTO.getDestinationLongitude()); // Changed to setDeliveryLongitude
    } 
   
       
        if (deliveryDTO.getRecipientName() != null) {
            delivery.setRecipientName(deliveryDTO.getRecipientName());
        }
        
        if (deliveryDTO.getRecipientPhone() != null) {
            delivery.setRecipientPhone(deliveryDTO.getRecipientPhone());
        }
        
        if (deliveryDTO.getDeliveryInstructions() != null) {
            delivery.setDeliveryInstructions(deliveryDTO.getDeliveryInstructions());
        }
        
        // Change pickup point if specified and delivery is still pending
        if (deliveryDTO.getPickupPointId() != null && delivery.getStatus() == Delivery.DeliveryStatus.PENDING) {
            PickupPoint pickupPoint = pickupPointRepository.findById(deliveryDTO.getPickupPointId())
                    .orElseThrow(() -> new RuntimeException("Pickup point not found with ID: " + deliveryDTO.getPickupPointId()));
            delivery.setPickupPoint(pickupPoint);
        }
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery updated with ID: {}", updatedDelivery.getId());
        
        return modelMapper.map(updatedDelivery, DeliveryDTO.class);
    }
    
    @Transactional
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + id));
        
        // Only allow deletion if delivery is in PENDING or CANCELLED status
        if (delivery.getStatus() != Delivery.DeliveryStatus.PENDING && delivery.getStatus() != Delivery.DeliveryStatus.CANCELLED) {
            throw new RuntimeException("Cannot delete delivery with status: " + delivery.getStatus());
        }
        
        deliveryRepository.deleteById(id);
        log.info("Delivery deleted with ID: {}", id);
    }
}
