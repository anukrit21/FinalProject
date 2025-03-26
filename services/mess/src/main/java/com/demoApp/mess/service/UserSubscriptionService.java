package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.mess.dto.UserSubscriptionDTO;
import com.demoApp.mess.entity.Subscription;
import com.demoApp.mess.entity.UserSubscription;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.SubscriptionRepository;
import com.demoApp.mess.repository.UserSubscriptionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {
    
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;
    
    public List<UserSubscription> getAllUserSubscriptions() {
        return userSubscriptionRepository.findAll();
    }
    
    public UserSubscription getUserSubscriptionById(Long id) {
        return userSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User subscription not found with id: " + id));
    }
    
    public List<UserSubscription> getUserSubscriptionsByUserId(Long userId) {
        return userSubscriptionRepository.findByUserId(userId);
    }
    
    public List<UserSubscription> getActiveUserSubscriptionsByUserId(Long userId) {
        return userSubscriptionRepository.findActiveSubscriptionsByUserId(userId, LocalDate.now());
    }
    
    public List<UserSubscription> getUserSubscriptionsByMessId(Long messId) {
        return userSubscriptionRepository.findBySubscriptionMessId(messId);
    }
    
    public List<UserSubscription> getActiveUserSubscriptionsByMessId(Long messId) {
        return userSubscriptionRepository.findActiveSubscriptionsByMessId(messId, LocalDate.now());
    }
    
    @Transactional
    public UserSubscription createUserSubscription(UserSubscriptionDTO userSubscriptionDTO) {
        // Find the subscription
        Subscription subscription = subscriptionRepository.findById(userSubscriptionDTO.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " 
                        + userSubscriptionDTO.getSubscriptionId()));
        
        // Create the user subscription entity
        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUserId(userSubscriptionDTO.getUserId());
        userSubscription.setSubscription(subscription);
        userSubscription.setStartDate(userSubscriptionDTO.getStartDate());
        userSubscription.setEndDate(userSubscriptionDTO.getEndDate());
        userSubscription.setStatus(userSubscriptionDTO.getStatus());
        userSubscription.setPaymentCompleted(userSubscriptionDTO.isPaymentCompleted());
        userSubscription.setPaymentTransactionId(userSubscriptionDTO.getPaymentTransactionId());
        userSubscription.setMealDeliveryAddress(userSubscriptionDTO.getMealDeliveryAddress());
        userSubscription.setMealDeliveryInstructions(userSubscriptionDTO.getMealDeliveryInstructions());
        
        return userSubscriptionRepository.save(userSubscription);
    }
    
    @Transactional
    public UserSubscription updateUserSubscription(Long id, UserSubscriptionDTO userSubscriptionDTO) {
        // Find the existing user subscription
        UserSubscription userSubscription = userSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User subscription not found with id: " + id));
        
        // Check if the subscription ID has changed
        if (userSubscriptionDTO.getSubscriptionId() != null && 
            !userSubscription.getSubscription().getId().equals(userSubscriptionDTO.getSubscriptionId())) {
            
            // Find the new subscription
            Subscription subscription = subscriptionRepository.findById(userSubscriptionDTO.getSubscriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " 
                            + userSubscriptionDTO.getSubscriptionId()));
            
            userSubscription.setSubscription(subscription);
        }
        
        // Update other fields
        if (userSubscriptionDTO.getUserId() != null) {
            userSubscription.setUserId(userSubscriptionDTO.getUserId());
        }
        if (userSubscriptionDTO.getStartDate() != null) {
            userSubscription.setStartDate(userSubscriptionDTO.getStartDate());
        }
        if (userSubscriptionDTO.getEndDate() != null) {
            userSubscription.setEndDate(userSubscriptionDTO.getEndDate());
        }
        if (userSubscriptionDTO.getStatus() != null) {
            userSubscription.setStatus(userSubscriptionDTO.getStatus());
        }
        userSubscription.setPaymentCompleted(userSubscriptionDTO.isPaymentCompleted());
        if (userSubscriptionDTO.getPaymentTransactionId() != null) {
            userSubscription.setPaymentTransactionId(userSubscriptionDTO.getPaymentTransactionId());
        }
        if (userSubscriptionDTO.getMealDeliveryAddress() != null) {
            userSubscription.setMealDeliveryAddress(userSubscriptionDTO.getMealDeliveryAddress());
        }
        if (userSubscriptionDTO.getMealDeliveryInstructions() != null) {
            userSubscription.setMealDeliveryInstructions(userSubscriptionDTO.getMealDeliveryInstructions());
        }
        
        return userSubscriptionRepository.save(userSubscription);
    }
    
    @Transactional
    public void cancelUserSubscription(Long id) {
        UserSubscription userSubscription = userSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User subscription not found with id: " + id));
        
        // Check if the subscription was created more than 6 hours ago
        if (userSubscription.getCreatedAt().plusHours(6).isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("Subscription cancellation not allowed after 6 hours from creation time");
        }
        
        userSubscription.setStatus(UserSubscription.SubscriptionStatus.CANCELLED);
        userSubscriptionRepository.save(userSubscription);
    }
    
    @Transactional
    public void deleteUserSubscription(Long id) {
        UserSubscription userSubscription = userSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User subscription not found with id: " + id));
        
        userSubscriptionRepository.delete(userSubscription);
    }
    
    /**
     * Check for expired subscriptions daily at midnight and update their status
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateExpiredSubscriptions() {
        List<UserSubscription> expiredSubscriptions = userSubscriptionRepository.findExpiredSubscriptions(LocalDate.now());
        
        for (UserSubscription subscription : expiredSubscriptions) {
            subscription.setStatus(UserSubscription.SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);
        }
    }
    
    /**
     * Convert a UserSubscription entity to a Response DTO
     */
    public UserSubscriptionDTO.UserSubscriptionResponseDTO convertToResponseDTO(UserSubscription userSubscription) {
        UserSubscriptionDTO.UserSubscriptionResponseDTO responseDTO = new UserSubscriptionDTO.UserSubscriptionResponseDTO();
        responseDTO.setId(userSubscription.getId());
        responseDTO.setUserId(userSubscription.getUserId());
        responseDTO.setSubscription(modelMapper.map(userSubscription.getSubscription(), com.demoApp.mess.dto.SubscriptionDTO.class));
        responseDTO.setStartDate(userSubscription.getStartDate());
        responseDTO.setEndDate(userSubscription.getEndDate());
        responseDTO.setStatus(userSubscription.getStatus());
        responseDTO.setPaymentCompleted(userSubscription.isPaymentCompleted());
        responseDTO.setPaymentTransactionId(userSubscription.getPaymentTransactionId());
        responseDTO.setMealDeliveryAddress(userSubscription.getMealDeliveryAddress());
        responseDTO.setMealDeliveryInstructions(userSubscription.getMealDeliveryInstructions());
        responseDTO.setDaysRemaining(userSubscription.getDaysRemaining());
        responseDTO.setActive(userSubscription.isActive());
        
        return responseDTO;
    }
    
    /**
     * Convert a list of UserSubscription entities to Response DTOs
     */
    public List<UserSubscriptionDTO.UserSubscriptionResponseDTO> convertToResponseDTOs(List<UserSubscription> userSubscriptions) {
        return userSubscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
} 