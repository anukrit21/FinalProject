package com.demoApp.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for payment method operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethodDTO {

    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Payment method type is required")
    private String type; // card, bank_account, etc.
    
    private String lastFour;
    
    private Integer expiryMonth;
    
    private Integer expiryYear;
    
    private String brand;
    
    private String stripePaymentMethodId;
    
    private String stripeCustomerId;
    
    // Used for creation only - not returned in responses
    private String stripeToken;
    
    private String name;
    
    private String email;
    
    private Boolean isDefault;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 