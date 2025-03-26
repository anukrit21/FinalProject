package com.demoApp.subscription.service;

import com.demoApp.subscription.dto.SubscriptionDTO;
import com.demoApp.subscription.entity.Subscription;
import com.demoApp.subscription.exception.ResourceNotFoundException;
import com.demoApp.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;
    
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
    
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findByStatus(Subscription.Status.ACTIVE);
    }
    
    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
    }
    
    @Transactional
    public Subscription createSubscription(SubscriptionDTO subscriptionDTO) {
        Subscription subscription = modelMapper.map(subscriptionDTO, Subscription.class);
        return subscriptionRepository.save(subscription);
    }
    
    @Transactional
    public Subscription updateSubscription(Long id, SubscriptionDTO subscriptionDTO) {
        // Check if the subscription exists
        Subscription existingSubscription = getSubscriptionById(id);
        
        // Update fields from DTO
        if (subscriptionDTO.getName() != null) {
            existingSubscription.setName(subscriptionDTO.getName());
        }
        if (subscriptionDTO.getDescription() != null) {
            existingSubscription.setDescription(subscriptionDTO.getDescription());
        }
        if (subscriptionDTO.getPrice() != null) {
            existingSubscription.setPrice(subscriptionDTO.getPrice());
        }
        if (subscriptionDTO.getType() != null) {
            existingSubscription.setType(subscriptionDTO.getType());
        }
        if (subscriptionDTO.getStatus() != null) {
            existingSubscription.setStatus(subscriptionDTO.getStatus());
        }
        if (subscriptionDTO.getSubscriptionPeriod() != null) {
            existingSubscription.setSubscriptionPeriod(subscriptionDTO.getSubscriptionPeriod());
        }
        if (subscriptionDTO.getMealType() != null) {
            existingSubscription.setMealType(subscriptionDTO.getMealType());
        }
        if (subscriptionDTO.getOffice() != null) {
            existingSubscription.setOffice(subscriptionDTO.getOffice());
        }
        if (subscriptionDTO.getMealPerPortion() != null) {
            existingSubscription.setMealPerPortion(subscriptionDTO.getMealPerPortion());
        }
        if (subscriptionDTO.getMealsPerWeek() != null) {
            existingSubscription.setMealsPerWeek(subscriptionDTO.getMealsPerWeek());
        }
        if (subscriptionDTO.getDeliveryDays() != null) {
            existingSubscription.setDeliveryDays(subscriptionDTO.getDeliveryDays());
        }
        
        return subscriptionRepository.save(existingSubscription);
    }
    
    @Transactional
    public void deleteSubscription(Long id) {
        // Check if the subscription exists
        Subscription subscription = getSubscriptionById(id);
        
        // Delete it
        subscriptionRepository.delete(subscription);
    }
    
    @Transactional
    public Subscription toggleSubscriptionStatus(Long id) {
        // Check if the subscription exists
        Subscription subscription = getSubscriptionById(id);
        
        // Toggle its status
        if (subscription.getStatus() == Subscription.Status.ACTIVE) {
            subscription.setStatus(Subscription.Status.INACTIVE);
        } else {
            subscription.setStatus(Subscription.Status.ACTIVE);
        }
        
        return subscriptionRepository.save(subscription);
    }
    
    public SubscriptionDTO convertToDTO(Subscription subscription) {
        return modelMapper.map(subscription, SubscriptionDTO.class);
    }
    
    public List<SubscriptionDTO> convertToDTOs(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
} 