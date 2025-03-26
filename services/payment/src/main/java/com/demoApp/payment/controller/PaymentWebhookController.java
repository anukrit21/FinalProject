package com.demoApp.payment.controller;

import com.demoApp.payment.config.StripeConfig;
import com.demoApp.payment.model.PaymentStatus;
import com.demoApp.payment.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling Stripe webhook events
 */
@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;

    /**
     * Handle Stripe webhook events
     */
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        log.info("Received Stripe webhook event");
        
        // Validate webhook signature
        Event event = stripeConfig.validateWebhookSignature(payload, sigHeader);
        if (event == null) {
            log.error("Invalid webhook signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
        
        try {
            // Parse the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
            
            if (stripeObject == null) {
                log.error("Failed to deserialize Stripe object");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to deserialize Stripe object");
            }
            
            // Handle different event types
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded((PaymentIntent) stripeObject);
                    break;
                    
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed((PaymentIntent) stripeObject);
                    break;
                
                case "charge.refunded":
                    // Could add handling for refund events
                    log.info("Refund event received: {}", event.getId());
                    break;
                    
                // Add more event types as needed
                
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
            
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    /**
     * Handle payment intent succeeded event
     */
    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        log.info("Payment intent succeeded: {}", paymentIntent.getId());
        
        try {
            // Update payment status
            paymentService.updatePaymentStatus(
                    paymentIntent.getId(),
                    PaymentStatus.COMPLETED,
                    null,
                    null
            );
        } catch (Exception e) {
            log.error("Error handling payment intent succeeded", e);
        }
    }

    /**
     * Handle payment intent failed event
     */
    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        log.info("Payment intent failed: {}", paymentIntent.getId());
        
        try {
            // Get failure information
            String failureMessage = paymentIntent.getLastPaymentError() != null 
                    ? paymentIntent.getLastPaymentError().getMessage() 
                    : "Payment failed";
            
            String failureCode = paymentIntent.getLastPaymentError() != null 
                    ? paymentIntent.getLastPaymentError().getCode() 
                    : "unknown";
            
            // Update payment status
            paymentService.updatePaymentStatus(
                    paymentIntent.getId(),
                    PaymentStatus.FAILED,
                    failureMessage,
                    failureCode
            );
        } catch (Exception e) {
            log.error("Error handling payment intent failed", e);
        }
    }
} 