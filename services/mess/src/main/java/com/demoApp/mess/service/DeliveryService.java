package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.mess.dto.DeliveryDTO;
import com.demoApp.mess.entity.Delivery;
import com.demoApp.mess.entity.DeliveryPerson;
import com.demoApp.mess.entity.User;
import com.demoApp.mess.enums.DeliveryStatus;
import com.demoApp.mess.exception.BadRequestException;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.DeliveryPersonRepository;
import com.demoApp.mess.repository.DeliveryRepository;
import com.demoApp.mess.repository.UserRepository;
import com.demoApp.mess.security.UserSecurity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserSecurity userSecurity;
    private final EmailService emailService;

    /**
     * Get all deliveries (admin only)
     */
    public List<DeliveryDTO> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get deliveries with pagination and search
     */
    public Map<String, Object> getDeliveriesPaged(int page, int size, DeliveryStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Delivery> deliveriesPage;
        
        if (status != null) {
            deliveriesPage = deliveryRepository.findByStatus(status, pageable);
        } else {
            deliveriesPage = deliveryRepository.findAll(pageable);
        }
        
        List<DeliveryDTO> deliveryDTOs = deliveriesPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("deliveries", deliveryDTOs);
        response.put("currentPage", deliveriesPage.getNumber());
        response.put("totalItems", deliveriesPage.getTotalElements());
        response.put("totalPages", deliveriesPage.getTotalPages());
        
        return response;
    }

    /**
     * Get deliveries for current user (based on role)
     */
    public Map<String, Object> getDeliveriesForCurrentUser(int page, int size, DeliveryStatus status) {
        User currentUser = userSecurity.getCurrentUser();
        if (currentUser == null) {
            throw new BadRequestException("No authenticated user found");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Delivery> deliveriesPage;
        
        if (userSecurity.isAdmin()) {
            // Admin can see all deliveries
            if (status != null) {
                deliveriesPage = deliveryRepository.findByStatus(status, pageable);
            } else {
                deliveriesPage = deliveryRepository.findAll(pageable);
            }
        } else if (userSecurity.isMess()) {
            // Mess can see its own deliveries
            if (status != null) {
                deliveriesPage = deliveryRepository.findByMessAndStatus(currentUser, status, pageable);
            } else {
                deliveriesPage = deliveryRepository.findByMess(currentUser, pageable);
            }
        } else {
            // Regular user can see their customer deliveries
            if (status != null) {
                deliveriesPage = deliveryRepository.findByCustomerAndStatus(currentUser, status, pageable);
            } else {
                deliveriesPage = deliveryRepository.findByCustomer(currentUser, pageable);
            }
        }
        
        List<DeliveryDTO> deliveryDTOs = deliveriesPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("deliveries", deliveryDTOs);
        response.put("currentPage", deliveriesPage.getNumber());
        response.put("totalItems", deliveriesPage.getTotalElements());
        response.put("totalPages", deliveriesPage.getTotalPages());
        
        return response;
    }

    /**
     * Get delivery by ID
     */
    public DeliveryDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        
        return convertToDTO(delivery);
    }

    /**
     * Get delivery by tracking code
     */
    public DeliveryDTO getDeliveryByTrackingCode(String trackingCode) {
        Delivery delivery = deliveryRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with tracking code: " + trackingCode));
        
        return convertToDTO(delivery);
    }

    /**
     * Create a new delivery
     */
    @Transactional
    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) {
        // Validate required fields
        if (deliveryDTO.getOrderReferenceId() == null || 
                deliveryDTO.getOrderReferenceType() == null || 
                deliveryDTO.getOrderReferenceType().isEmpty()) {
            throw new BadRequestException("Order reference details are required");
        }
        
        if (deliveryDTO.getDeliveryAddress() == null || deliveryDTO.getDeliveryAddress().isEmpty()) {
            throw new BadRequestException("Delivery address is required");
        }
        
        if (deliveryDTO.getContactName() == null || deliveryDTO.getContactName().isEmpty() || 
                deliveryDTO.getContactPhone() == null || deliveryDTO.getContactPhone().isEmpty()) {
            throw new BadRequestException("Contact details are required");
        }
        
        // Set up customer
        User customer;
        if (deliveryDTO.getCustomerId() != null) {
            customer = userRepository.findById(deliveryDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + deliveryDTO.getCustomerId()));
        } else {
            // Use current user as customer
            customer = userSecurity.getCurrentUser();
            if (customer == null) {
                throw new BadRequestException("No authenticated user found");
            }
        }
        
        // Set up mess
        User mess;
        if (deliveryDTO.getMessId() != null) {
            mess = userRepository.findById(deliveryDTO.getMessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + deliveryDTO.getMessId()));
        } else {
            // Must provide a mess ID
            throw new BadRequestException("Mess ID is required");
        }
        
        // Set up delivery person (if provided)
        DeliveryPerson deliveryPerson = null;
        if (deliveryDTO.getDeliveryPersonId() != null) {
            deliveryPerson = deliveryPersonRepository.findById(deliveryDTO.getDeliveryPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + deliveryDTO.getDeliveryPersonId()));
        }
        
        // Create and save the delivery
        Delivery delivery = new Delivery();
        delivery.setOrderReferenceId(deliveryDTO.getOrderReferenceId());
        delivery.setOrderReferenceType(deliveryDTO.getOrderReferenceType());
        delivery.setCustomer(customer);
        delivery.setMess(mess);
        delivery.setDeliveryPerson(deliveryPerson);
        
        // Delivery location details
        delivery.setDeliveryAddress(deliveryDTO.getDeliveryAddress());
        delivery.setDeliveryCity(deliveryDTO.getDeliveryCity());
        delivery.setDeliveryState(deliveryDTO.getDeliveryState());
        delivery.setDeliveryPostalCode(deliveryDTO.getDeliveryPostalCode());
        delivery.setDeliveryInstructions(deliveryDTO.getDeliveryInstructions());
        
        // Contact info
        delivery.setContactName(deliveryDTO.getContactName());
        delivery.setContactPhone(deliveryDTO.getContactPhone());
        
        // Coordinates if provided
        delivery.setDeliveryLatitude(deliveryDTO.getDeliveryLatitude());
        delivery.setDeliveryLongitude(deliveryDTO.getDeliveryLongitude());
        delivery.setPickupLatitude(deliveryDTO.getPickupLatitude());
        delivery.setPickupLongitude(deliveryDTO.getPickupLongitude());
        
        // Scheduled times
        delivery.setScheduledPickupTime(deliveryDTO.getScheduledPickupTime());
        delivery.setScheduledDeliveryTime(deliveryDTO.getScheduledDeliveryTime());
        
        // Set status based on whether a delivery person is assigned
        if (deliveryPerson != null) {
            delivery.setStatus(DeliveryStatus.ASSIGNED);
        } else {
            delivery.setStatus(DeliveryStatus.PENDING);
        }
        
        // Set creator
        delivery.setCreatedBy(userSecurity.getCurrentUserId());
        
        Delivery savedDelivery = deliveryRepository.save(delivery);
        
        return convertToDTO(savedDelivery);
    }

    /**
     * Assign delivery person to a delivery
     */
    @Transactional
    public DeliveryDTO assignDeliveryPerson(Long deliveryId, Long deliveryPersonId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + deliveryPersonId));
        
        // Check if delivery person belongs to the correct mess
        if (!deliveryPerson.getMess().getId().equals(delivery.getMess().getId())) {
            throw new BadRequestException("Delivery person does not belong to the delivery's mess");
        }
        
        // Check if delivery person is active
        if (!deliveryPerson.isActive()) {
            throw new BadRequestException("Delivery person is not active");
        }
        
        // Update the delivery
        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setUpdatedBy(userSecurity.getCurrentUserId());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        
        return convertToDTO(updatedDelivery);
    }

    /**
     * Update delivery status
     */
    @Transactional
    public DeliveryDTO updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        // Validate status transition
        validateStatusTransition(delivery.getStatus(), status);
        
        // Update timestamps based on status
        switch (status) {
            case PICKED_UP:
                delivery.setActualPickupTime(LocalDateTime.now());
                break;
            case DELIVERED:
                delivery.setActualDeliveryTime(LocalDateTime.now());
                break;
            default:
                break;
        }
        
        // Update status
        delivery.setStatus(status);
        delivery.setUpdatedBy(userSecurity.getCurrentUserId());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        
        // Notify customer for important status changes (e.g., PICKED_UP, DELIVERED, FAILED)
        if (status == DeliveryStatus.PICKED_UP || status == DeliveryStatus.DELIVERED || status == DeliveryStatus.FAILED) {
            notifyCustomerAboutDeliveryStatus(updatedDelivery);
        }
        
        return convertToDTO(updatedDelivery);
    }

    /**
     * Update delivery location
     */
    @Transactional
    public DeliveryDTO updateDeliveryLocation(Long deliveryId, Double latitude, Double longitude) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        delivery.setCurrentLatitude(latitude);
        delivery.setCurrentLongitude(longitude);
        delivery.setUpdatedBy(userSecurity.getCurrentUserId());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        
        return convertToDTO(updatedDelivery);
    }

    /**
     * Add delivery feedback and rating
     */
    @Transactional
    public DeliveryDTO addFeedbackAndRating(Long deliveryId, Integer rating, String feedback) {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        // Check if delivery is completed
        if (delivery.getStatus() != DeliveryStatus.DELIVERED) {
            throw new BadRequestException("Feedback can only be provided for delivered items");
        }
        
        delivery.setRating(rating);
        delivery.setFeedback(feedback);
        delivery.setUpdatedBy(userSecurity.getCurrentUserId());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        
        // Update delivery person rating if applicable
        if (rating != null && delivery.getDeliveryPerson() != null) {
            updateDeliveryPersonRating(delivery.getDeliveryPerson().getId(), rating);
        }
        
        return convertToDTO(updatedDelivery);
    }

    /**
     * Report delivery issue
     */
    @Transactional
    public DeliveryDTO reportIssue(Long deliveryId, String issueDescription) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        delivery.setIssueDescription(issueDescription);
        delivery.setUpdatedBy(userSecurity.getCurrentUserId());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        
        return convertToDTO(updatedDelivery);
    }

    /**
     * Resolve delivery issue
     */
    @Transactional
    public DeliveryDTO resolveIssue(Long deliveryId, String resolution) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        if (delivery.getIssueDescription() == null || delivery.getIssueDescription().isEmpty()) {
            throw new BadRequestException("No issue reported for this delivery");
        }
        
        delivery.setResolution(resolution);
        delivery.setUpdatedBy(userSecurity.getCurrentUserId());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        
        return convertToDTO(updatedDelivery);
    }

    /**
     * Cancel delivery
     */
    @Transactional
    public DeliveryDTO cancelDelivery(Long deliveryId, String reason) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));
        
        // Check if delivery can be cancelled
        if (delivery.getStatus() == DeliveryStatus.DELIVERED || 
                delivery.getStatus() == DeliveryStatus.FAILED ||
                delivery.getStatus() == DeliveryStatus.CANCELLED) {
            throw new BadRequestException("Delivery cannot be cancelled in its current state");
        }
        
        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery.setIssueDescription(reason);
        delivery.setUpdatedBy(userSecurity.getCurrentUserId());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        
        // Notify customer
        notifyCustomerAboutDeliveryStatus(updatedDelivery);
        
        return convertToDTO(updatedDelivery);
    }

    /**
     * Get deliveries statistics
     */
    public Map<String, Object> getDeliveryStatistics(Long messId) {
        User mess = null;
        if (messId != null) {
            mess = userRepository.findById(messId)
                    .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + messId));
        } else if (userSecurity.isMess()) {
            mess = userSecurity.getCurrentUser();
        } else if (!userSecurity.isAdmin()) {
            throw new BadRequestException("Unauthorized access to delivery statistics");
        }
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Get delivery counts by status
        Map<DeliveryStatus, Long> statusCounts = new HashMap<>();
        if (mess != null) {
            List<Object[]> statusCountsList = deliveryRepository.countDeliveriesByStatusForMess(mess);
            statusCountsList.forEach(row -> {
                DeliveryStatus status = (DeliveryStatus) row[0];
                Long count = (Long) row[1];
                statusCounts.put(status, count);
            });
        } else {
            // Admin can see all counts
            Arrays.stream(DeliveryStatus.values()).forEach(status -> {
                long count = deliveryRepository.findByStatus(status).size();
                statusCounts.put(status, count);
            });
        }
        
        statistics.put("statusCounts", statusCounts);
        
        // Get daily delivery counts for the last 30 days
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        if (mess != null) {
            List<Object[]> dailyCounts = deliveryRepository.countDeliveriesByDayForMess(mess.getId(), startDate);
            statistics.put("dailyCounts", dailyCounts);
        }
        
        // Get count of active delivery people (for mess)
        if (mess != null) {
            long activeDeliveryPersonsCount = deliveryPersonRepository.findByMessAndActiveTrue(mess).size();
            statistics.put("activeDeliveryPersonsCount", activeDeliveryPersonsCount);
        }
        
        // Get average rating
        if (mess != null) {
            List<Delivery> messDeliveries = deliveryRepository.findByMess(mess);
            double averageRating = messDeliveries.stream()
                    .filter(d -> d.getRating() != null)
                    .mapToInt(Delivery::getRating)
                    .average()
                    .orElse(0.0);
            statistics.put("averageRating", averageRating);
        }
        
        return statistics;
    }

    /**
     * Get pending deliveries for assignment
     */
    public List<DeliveryDTO> getPendingDeliveriesForAssignment(Long messId) {
        User mess;
        if (messId != null) {
            mess = userRepository.findById(messId)
                    .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + messId));
        } else {
            mess = userSecurity.getCurrentUser();
            if (mess == null) {
                throw new BadRequestException("No authenticated user found");
            }
        }
        
        return deliveryRepository.findByMessAndStatus(mess, DeliveryStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper methods
     */
    private void validateStatusTransition(DeliveryStatus currentStatus, DeliveryStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // No change, always valid
        }
        
        switch (currentStatus) {
            case PENDING:
                if (newStatus != DeliveryStatus.ASSIGNED && newStatus != DeliveryStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from PENDING to " + newStatus);
                }
                break;
            case ASSIGNED:
                if (newStatus != DeliveryStatus.PICKED_UP && newStatus != DeliveryStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from ASSIGNED to " + newStatus);
                }
                break;
            case PICKED_UP:
                if (newStatus != DeliveryStatus.IN_TRANSIT && newStatus != DeliveryStatus.FAILED && newStatus != DeliveryStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from PICKED_UP to " + newStatus);
                }
                break;
            case IN_TRANSIT:
                if (newStatus != DeliveryStatus.DELIVERED && newStatus != DeliveryStatus.FAILED && newStatus != DeliveryStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from IN_TRANSIT to " + newStatus);
                }
                break;
            case DELIVERED:
                throw new BadRequestException("Cannot change status from DELIVERED");
            case FAILED:
                if (newStatus != DeliveryStatus.ASSIGNED && newStatus != DeliveryStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from FAILED to " + newStatus);
                }
                break;
            case CANCELLED:
                throw new BadRequestException("Cannot change status from CANCELLED");
        }
    }

    private void updateDeliveryPersonRating(Long deliveryPersonId, Integer rating) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + deliveryPersonId));
        
        // Calculate new average rating
        Double currentAverage = deliveryPerson.getAverageRating() != null ? deliveryPerson.getAverageRating() : 0.0;
        Integer totalRatings = deliveryPerson.getTotalRatings() != null ? deliveryPerson.getTotalRatings() : 0;
        
        Double newAverage = ((currentAverage * totalRatings) + rating) / (totalRatings + 1);
        
        deliveryPerson.setAverageRating(newAverage);
        deliveryPerson.setTotalRatings(totalRatings + 1);
        deliveryPerson.setUpdatedBy(userSecurity.getCurrentUserId());
        
        deliveryPersonRepository.save(deliveryPerson);
    }

    private void notifyCustomerAboutDeliveryStatus(Delivery delivery) {
        if (delivery.getCustomer() == null || delivery.getCustomer().getEmail() == null) {
            return;
        }
        
        String customerEmail = delivery.getCustomer().getEmail();
        String customerName = delivery.getCustomer().getUsername();
        String messName = delivery.getMess() != null ? delivery.getMess().getUsername() : "the restaurant";
        String deliveryStatus = delivery.getStatus().toString();
        
        // Send email notification (this would be async)
        try {
            emailService.sendSubscriptionConfirmationEmail(customerEmail, customerName, "Delivery status: " + deliveryStatus, messName);
        } catch (Exception e) {
            // Log but don't fail the transaction
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    private DeliveryDTO convertToDTO(Delivery delivery) {
        DeliveryDTO dto = modelMapper.map(delivery, DeliveryDTO.class);
        
        // Set customer details
        if (delivery.getCustomer() != null) {
            dto.setCustomerId(delivery.getCustomer().getId());
            dto.setCustomerName(delivery.getCustomer().getUsername());
            // Assuming you have access to phone in User entity
            // dto.setCustomerPhone(delivery.getCustomer().getPhone());
        }
        
        // Set delivery person details
        if (delivery.getDeliveryPerson() != null) {
            dto.setDeliveryPersonId(delivery.getDeliveryPerson().getId());
            dto.setDeliveryPersonName(delivery.getDeliveryPerson().getName());
            dto.setDeliveryPersonPhone(delivery.getDeliveryPerson().getPhone());
        }
        
        // Set mess details
        if (delivery.getMess() != null) {
            dto.setMessId(delivery.getMess().getId());
            dto.setMessName(delivery.getMess().getUsername());
            // Assuming you have access to phone in User entity
            // dto.setMessPhone(delivery.getMess().getPhone());
        }
        
        return dto;
    }
} 