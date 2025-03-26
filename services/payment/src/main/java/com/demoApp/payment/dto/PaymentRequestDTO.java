package com.demoApp.payment.dto;

import com.demoApp.payment.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for payment request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private BigDecimal tax;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String paymentMethodId;

    private String orderReference;

    private Long productId;

    private Long subscriptionId;

    private Long merchantId;

    private Long ownerId;

    private String metadata;

    private String customerName;

    private String customerEmail;

    private String billingAddress;

    private String shippingAddress;

    private boolean savePaymentMethod = false;

    // For creating a setup intent instead of a payment
    private boolean setupIntent = false;

    // For initiating recurring payments
    private boolean recurring = false;
} 