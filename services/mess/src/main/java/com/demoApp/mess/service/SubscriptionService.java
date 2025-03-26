package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.demoApp.mess.dto.SubscriptionDTO;
import com.demoApp.mess.entity.Mess;
import com.demoApp.mess.entity.Subscription;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.MessRepository;
import com.demoApp.mess.repository.SubscriptionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final MessRepository messRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;
    
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
    
    public Map<String, Object> getAllSubscriptionsPaged(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<Subscription> subscriptionsPage;
        if (search != null && !search.isEmpty()) {
            subscriptionsPage = subscriptionRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            subscriptionsPage = subscriptionRepository.findAll(pageable);
        }
        
        List<SubscriptionDTO> subscriptionDTOs = subscriptionsPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("subscriptions", subscriptionDTOs);
        response.put("currentPage", subscriptionsPage.getNumber());
        response.put("totalItems", subscriptionsPage.getTotalElements());
        response.put("totalPages", subscriptionsPage.getTotalPages());
        
        return response;
    }
    
    public List<Subscription> getSubscriptionsByMessId(Long messId) {
        return subscriptionRepository.findByMessId(messId);
    }
    
    public List<SubscriptionDTO> getSubscriptionDTOsByMessId(Long messId) {
        return getSubscriptionsByMessId(messId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<Subscription> getActiveSubscriptionsByMessId(Long messId) {
        return subscriptionRepository.findByMessIdAndActive(messId, true);
    }
    
    public List<SubscriptionDTO> getActiveSubscriptionDTOsByMessId(Long messId) {
        return getActiveSubscriptionsByMessId(messId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
    }
    
    public SubscriptionDTO getSubscriptionDTOById(Long id) {
        Subscription subscription = getSubscriptionById(id);
        return convertToDTO(subscription);
    }
    
    public Long getMessIdBySubscriptionId(Long subscriptionId) {
        Subscription subscription = getSubscriptionById(subscriptionId);
        return subscription.getMess().getId();
    }
    
    @Transactional
    public Subscription createSubscription(SubscriptionDTO subscriptionDTO) {
        Mess mess = messRepository.findById(subscriptionDTO.getMessId())
                .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + subscriptionDTO.getMessId()));
        
        Subscription subscription = new Subscription();
        subscription.setName(subscriptionDTO.getName());
        subscription.setDescription(subscriptionDTO.getDescription());
        subscription.setPrice(subscriptionDTO.getPrice());
        subscription.setDurationDays(subscriptionDTO.getDurationDays());
        subscription.setType(subscriptionDTO.getType());
        subscription.setActive(subscriptionDTO.isActive());
        subscription.setMess(mess);
        subscription.setImageUrl(subscriptionDTO.getImageUrl());
        subscription.setMealsPerWeek(subscriptionDTO.getMealsPerWeek());
        subscription.setDeliveryDays(subscriptionDTO.getDeliveryDays());
        subscription.setMealTypes(subscriptionDTO.getMealTypes());
        subscription.setDietaryOptions(subscriptionDTO.getDietaryOptions());
        
        return subscriptionRepository.save(subscription);
    }
    
    public SubscriptionDTO createSubscriptionDTO(SubscriptionDTO subscriptionDTO) {
        Subscription subscription = createSubscription(subscriptionDTO);
        return convertToDTO(subscription);
    }
    
    @Transactional
    public Subscription updateSubscription(Long id, SubscriptionDTO subscriptionDTO) {
        Subscription subscription = getSubscriptionById(id);
        
        if (subscriptionDTO.getName() != null) {
            subscription.setName(subscriptionDTO.getName());
        }
        if (subscriptionDTO.getDescription() != null) {
            subscription.setDescription(subscriptionDTO.getDescription());
        }
        if (subscriptionDTO.getPrice() != null) {
            subscription.setPrice(subscriptionDTO.getPrice());
        }
        if (subscriptionDTO.getDurationDays() != null) {
            subscription.setDurationDays(subscriptionDTO.getDurationDays());
        }
        if (subscriptionDTO.getType() != null) {
            subscription.setType(subscriptionDTO.getType());
        }
        subscription.setActive(subscriptionDTO.isActive());
        
        // Update additional fields
        if (subscriptionDTO.getImageUrl() != null) {
            subscription.setImageUrl(subscriptionDTO.getImageUrl());
        }
        if (subscriptionDTO.getMealsPerWeek() != null) {
            subscription.setMealsPerWeek(subscriptionDTO.getMealsPerWeek());
        }
        if (subscriptionDTO.getDeliveryDays() != null) {
            subscription.setDeliveryDays(subscriptionDTO.getDeliveryDays());
        }
        if (subscriptionDTO.getMealTypes() != null) {
            subscription.setMealTypes(subscriptionDTO.getMealTypes());
        }
        if (subscriptionDTO.getDietaryOptions() != null) {
            subscription.setDietaryOptions(subscriptionDTO.getDietaryOptions());
        }
        
        // Update the mess only if it has changed
        if (subscriptionDTO.getMessId() != null && 
            !subscription.getMess().getId().equals(subscriptionDTO.getMessId())) {
            
            Mess mess = messRepository.findById(subscriptionDTO.getMessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + subscriptionDTO.getMessId()));
            
            subscription.setMess(mess);
        }
        
        return subscriptionRepository.save(subscription);
    }
    
    public SubscriptionDTO updateSubscriptionDTO(Long id, SubscriptionDTO subscriptionDTO) {
        Subscription subscription = updateSubscription(id, subscriptionDTO);
        return convertToDTO(subscription);
    }
    
    @Transactional
    public void deleteSubscription(Long id) {
        Subscription subscription = getSubscriptionById(id);
        
        // Delete the image if it exists
        if (subscription.getImageUrl() != null && !subscription.getImageUrl().isEmpty()) {
            fileStorageService.deleteFile(subscription.getImageUrl());
        }
        
        subscriptionRepository.delete(subscription);
    }
    
    public List<Subscription> getSubscriptionsByType(Long messId, Subscription.SubscriptionType type) {
        return subscriptionRepository.findByMessIdAndType(messId, type);
    }
    
    public List<SubscriptionDTO> getSubscriptionDTOsByType(Long messId, Subscription.SubscriptionType type) {
        return getSubscriptionsByType(messId, type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public String uploadAndSetSubscriptionImage(Long id, MultipartFile image) {
        Subscription subscription = getSubscriptionById(id);
        
        // Delete the old image if it exists
        if (subscription.getImageUrl() != null && !subscription.getImageUrl().isEmpty()) {
            fileStorageService.deleteFile(subscription.getImageUrl());
        }
        
        // Upload the new image
        String imageUrl = fileStorageService.uploadSubscriptionImage(image);
        
        // Update the subscription with the new image URL
        subscription.setImageUrl(imageUrl);
        subscriptionRepository.save(subscription);
        
        return imageUrl;
    }
    
    @Transactional
    public boolean deleteSubscriptionImage(Long id) {
        Subscription subscription = getSubscriptionById(id);
        
        if (subscription.getImageUrl() != null && !subscription.getImageUrl().isEmpty()) {
            fileStorageService.deleteFile(subscription.getImageUrl());
            
            // Update the subscription to remove the image URL
            subscription.setImageUrl(null);
            subscriptionRepository.save(subscription);
            
            return true;
        }
        
        return false;
    }
    
    private SubscriptionDTO convertToDTO(Subscription subscription) {
        SubscriptionDTO dto = modelMapper.map(subscription, SubscriptionDTO.class);
        dto.setMessId(subscription.getMess().getId());
        return dto;
    }
}