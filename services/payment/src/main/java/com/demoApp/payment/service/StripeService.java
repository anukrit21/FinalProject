package com.demoApp.payment.service;

import com.demoApp.payment.entity.PaymentCustomer;
import com.demoApp.payment.exception.PaymentException;
import com.demoApp.payment.repository.PaymentCustomerRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.SetupIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for Stripe payment processing operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;
    
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;
    
    private final PaymentCustomerRepository paymentCustomerRepository;

    /**
     * Create a payment intent
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency, String description, 
                                             String customerId, Map<String, String> metadata) {
        try {
            log.info("Creating payment intent for amount: {}, currency: {}, customer: {}", 
                    amount, currency, customerId);
            
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setDescription(description)
                    .setCustomer(customerId)
                    .putAllMetadata(metadata);

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
            
            log.info("Payment intent created successfully: {}", paymentIntent.getId());
            return paymentIntent;
        } catch (StripeException e) {
            log.error("Error creating payment intent", e);
            throw new PaymentException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Create a setup intent for saving payment methods
     */
    public String createSetupIntent(String customerId, Map<String, String> metadata) {
        try {
            log.info("Creating setup intent for customer: {}", customerId);
            
            SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                    .setCustomer(customerId)
                    .putAllMetadata(metadata)
                    .build();

            SetupIntent setupIntent = SetupIntent.create(params);
            
            log.info("Setup intent created successfully: {}", setupIntent.getId());
            return setupIntent.getId();
        } catch (StripeException e) {
            log.error("Error creating setup intent", e);
            throw new PaymentException("Failed to create setup intent: " + e.getMessage(), e);
        }
    }

    /**
     * Process a refund
     */
    public Refund createRefund(String paymentIntentId, long amount, String reason, Map<String, String> metadata) {
        try {
            log.info("Creating refund for payment intent: {}, amount: {}", paymentIntentId, amount);
            
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setAmount(amount);
            
            if (reason != null && !reason.isEmpty()) {
                paramsBuilder.setReason(RefundCreateParams.Reason.valueOf(reason.toUpperCase()));
            }
            
            if (metadata != null && !metadata.isEmpty()) {
                paramsBuilder.putAllMetadata(metadata);
            }
            
            Refund refund = Refund.create(paramsBuilder.build());
            
            log.info("Refund created successfully: {}", refund.getId());
            return refund;
        } catch (StripeException e) {
            log.error("Error creating refund", e);
            throw new PaymentException("Failed to create refund: " + e.getMessage(), e);
        }
    }

    /**
     * Validate webhook signature
     */
    public Event validateWebhookSignature(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature for Stripe webhook", e);
            return null;
        }
    }
    
    /**
     * Get or create a Stripe customer for a user
     */
    @Transactional
    public String getOrCreateCustomerId(String userId, String email, String name) {
        log.info("Getting or creating Stripe customer for user: {}", userId);
        
        return paymentCustomerRepository.findByUserId(userId)
                .map(customer -> {
                    log.info("Found existing Stripe customer: {}", customer.getStripeCustomerId());
                    return customer.getStripeCustomerId();
                })
                .orElseGet(() -> {
                    try {
                        Map<String, Object> customerParams = new HashMap<>();
                        customerParams.put("email", email);
                        customerParams.put("name", name);
                        customerParams.put("metadata", Map.of("userId", userId));
                        
                        com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.create(customerParams);
                        String stripeCustomerId = stripeCustomer.getId();
                        
                        PaymentCustomer paymentCustomer = new PaymentCustomer();
                        paymentCustomer.setUserId(userId);
                        paymentCustomer.setEmail(email);
                        paymentCustomer.setName(name);
                        paymentCustomer.setStripeCustomerId(stripeCustomerId);
                        paymentCustomer.setCreatedAt(LocalDateTime.now());
                        paymentCustomer.setUpdatedAt(LocalDateTime.now());
                        paymentCustomerRepository.save(paymentCustomer);
                        
                        log.info("Created new Stripe customer: {}", stripeCustomerId);
                        return stripeCustomerId;
                    } catch (StripeException e) {
                        log.error("Error creating Stripe customer", e);
                        throw new PaymentException("Failed to create Stripe customer: " + e.getMessage(), e);
                    }
                });
    }
    
    /**
     * Save a payment method to Stripe
     */
    public String savePaymentMethod(String customerId, String stripeToken, String stripePaymentMethodId) {
        try {
            log.info("Saving payment method for customer: {}", customerId);
            
            if (stripePaymentMethodId != null && !stripePaymentMethodId.isEmpty()) {
                com.stripe.model.PaymentMethod paymentMethod = 
                        com.stripe.model.PaymentMethod.retrieve(stripePaymentMethodId);
                
                Map<String, Object> params = new HashMap<>();
                params.put("customer", customerId);
                paymentMethod.attach(params);
                
                log.info("Attached existing payment method: {}", stripePaymentMethodId);
                return stripePaymentMethodId;
            }
            
            if (stripeToken != null && !stripeToken.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("customer", customerId);
                params.put("source", stripeToken);
                
                com.stripe.model.Customer customer = com.stripe.model.Customer.retrieve(customerId);
                com.stripe.model.PaymentSource source = customer.getSources().create(params);
                
                log.info("Created payment method from token: {}", source.getId());
                return source.getId();
            }
            
            throw new PaymentException("Either stripeToken or stripePaymentMethodId must be provided");
        } catch (StripeException e) {
            log.error("Error saving payment method", e);
            throw new PaymentException("Failed to save payment method: " + e.getMessage(), e);
        }
    }
}
