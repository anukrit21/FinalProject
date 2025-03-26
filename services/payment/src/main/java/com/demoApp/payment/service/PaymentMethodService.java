package com.demoApp.payment.service;

import com.demoApp.payment.dto.PaymentMethodDTO;
import com.demoApp.payment.entity.PaymentMethod;
import com.demoApp.payment.exception.PaymentException;
import com.demoApp.payment.exception.ResourceNotFoundException;
import com.demoApp.payment.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for payment method operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final StripeService stripeService;
    private final ModelMapper modelMapper;

    /**
     * Save a new payment method
     */
    @Transactional
    public PaymentMethodDTO savePaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        log.info("Saving payment method for user: {}", paymentMethodDTO.getUserId());
        
        try {
            // Get or create Stripe customer
            String customerId = stripeService.getOrCreateCustomerId(
                    paymentMethodDTO.getUserId().toString(),
                    paymentMethodDTO.getEmail(),
                    paymentMethodDTO.getName());

            // Save payment method to Stripe
            String stripePaymentMethodId = stripeService.savePaymentMethod(
                    customerId,
                    paymentMethodDTO.getStripeToken(),
                    paymentMethodDTO.getStripePaymentMethodId());

            // Check if this is the first payment method for the user
            boolean isDefault = !paymentMethodRepository.existsByUserIdAndIsActiveTrue(paymentMethodDTO.getUserId());

            // Create payment method entity
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setUserId(paymentMethodDTO.getUserId());
            paymentMethod.setType(paymentMethodDTO.getType());
            paymentMethod.setLastFour(paymentMethodDTO.getLastFour());
            paymentMethod.setExpiryMonth(paymentMethodDTO.getExpiryMonth());
            paymentMethod.setExpiryYear(paymentMethodDTO.getExpiryYear());
            paymentMethod.setBrand(paymentMethodDTO.getBrand());
            paymentMethod.setStripePaymentMethodId(stripePaymentMethodId);
            paymentMethod.setStripeCustomerId(customerId);
            paymentMethod.setIsDefault(isDefault);
            paymentMethod.setIsActive(true);
            paymentMethod.setCreatedAt(LocalDateTime.now());
            paymentMethod.setUpdatedAt(LocalDateTime.now());

            PaymentMethod savedMethod = paymentMethodRepository.save(paymentMethod);
            log.info("Payment method saved with ID: {}", savedMethod.getId());
            
            return modelMapper.map(savedMethod, PaymentMethodDTO.class);
        } catch (Exception e) {
            log.error("Error saving payment method", e);
            throw new PaymentException("Failed to save payment method: " + e.getMessage());
        }
    }

    /**
     * Set a payment method as default
     */
    @Transactional
    public PaymentMethodDTO setDefaultPaymentMethod(Long paymentMethodId) {
        log.info("Setting payment method as default: {}", paymentMethodId);
        
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + paymentMethodId));

        if (!paymentMethod.getIsActive()) {
            throw new PaymentException("Cannot set inactive payment method as default");
        }

        // Set all user's payment methods as non-default
        List<PaymentMethod> userMethods = paymentMethodRepository.findByUserIdAndIsActiveTrue(paymentMethod.getUserId());
        for (PaymentMethod method : userMethods) {
            method.setIsDefault(false);
            method.setUpdatedAt(LocalDateTime.now());
        }
        paymentMethodRepository.saveAll(userMethods);

        // Set selected method as default
        paymentMethod.setIsDefault(true);
        paymentMethod.setUpdatedAt(LocalDateTime.now());
        PaymentMethod updatedMethod = paymentMethodRepository.save(paymentMethod);
        
        log.info("Payment method set as default: {}", updatedMethod.getId());
        return modelMapper.map(updatedMethod, PaymentMethodDTO.class);
    }

    /**
     * Delete a payment method (soft delete by setting isActive to false)
     */
    @Transactional
    public void deletePaymentMethod(Long paymentMethodId) {
        log.info("Deleting payment method: {}", paymentMethodId);
        
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + paymentMethodId));

        // Check if this is the default method
        boolean isDefault = paymentMethod.getIsDefault();
        
        // Set as inactive
        paymentMethod.setIsActive(false);
        paymentMethod.setIsDefault(false);
        paymentMethod.setUpdatedAt(LocalDateTime.now());
        paymentMethodRepository.save(paymentMethod);
        
        // If this was the default method, set another method as default if available
        if (isDefault) {
            List<PaymentMethod> activeMethods = paymentMethodRepository.findByUserIdAndIsActiveTrue(paymentMethod.getUserId());
            if (!activeMethods.isEmpty()) {
                PaymentMethod newDefault = activeMethods.get(0);
                newDefault.setIsDefault(true);
                newDefault.setUpdatedAt(LocalDateTime.now());
                paymentMethodRepository.save(newDefault);
                log.info("New default payment method set: {}", newDefault.getId());
            }
        }
        
        log.info("Payment method deleted: {}", paymentMethodId);
    }

    /**
     * Get a user's default payment method
     */
    public PaymentMethodDTO getDefaultPaymentMethod(Long userId) {
        log.info("Getting default payment method for user: {}", userId);
        
        PaymentMethod defaultMethod = paymentMethodRepository.findByUserIdAndIsDefaultTrueAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No default payment method found for user: " + userId));
        
        return modelMapper.map(defaultMethod, PaymentMethodDTO.class);
    }
} 