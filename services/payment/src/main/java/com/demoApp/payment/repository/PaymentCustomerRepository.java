package com.demoApp.payment.repository;

import com.demoApp.payment.entity.PaymentCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for payment customer operations
 */
@Repository
public interface PaymentCustomerRepository extends JpaRepository<PaymentCustomer, Long> {

    /**
     * Find a customer by user ID
     */
    Optional<PaymentCustomer> findByUserId(String userId);

    /**
     * Find a customer by Stripe customer ID
     */
    Optional<PaymentCustomer> findByStripeCustomerId(String stripeCustomerId);

    /**
     * Check if a customer exists for a user
     */
    boolean existsByUserId(String userId);
} 