package com.demoApp.payment.config;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Stripe integration
 */
@Configuration
@Slf4j
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;
    
    /**
     * Initialize Stripe API with API key
     */
    @PostConstruct
    public void init() {
        log.info("Initializing Stripe configuration...");
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe initialized successfully");
    }
    
    /**
     * Get webhook secret
     */
    public String getWebhookSecret() {
        return stripeWebhookSecret;
    }
    
    /**
     * Validate webhook signature
     * 
     * @param payload The raw request payload
     * @param sigHeader The Stripe signature header
     * @return The validated Stripe event or null if validation fails
     */
    public Event validateWebhookSignature(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature", e);
            return null;
        }
    }
} 