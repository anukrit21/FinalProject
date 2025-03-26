package com.demoApp.payment.service;

import com.demoApp.payment.dto.PaymentRequestDTO;
import com.demoApp.payment.dto.PaymentResponseDTO;
import com.demoApp.payment.dto.RefundRequestDTO;
import com.demoApp.payment.entity.Payment;
import com.demoApp.payment.exception.PaymentException;
import com.demoApp.payment.exception.ResourceNotFoundException;
import com.demoApp.payment.model.PaymentStatus;
import com.demoApp.payment.repository.PaymentRepository;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final ModelMapper modelMapper;

    /**
     * Process a payment.
     * Converts amount from BigDecimal to long and metadata to a Map.
     */
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO dto) {
        log.info("Processing payment for user ID: {}, amount: {}", dto.getUserId(), dto.getAmount());
        try {
            Payment payment = createPaymentRecord(dto);

            // Convert metadata string to Map<String, String>
            Map<String, String> metadataMap = new HashMap<>();
            if (dto.getMetadata() != null && !dto.getMetadata().isEmpty()) {
                metadataMap.put("metadata", dto.getMetadata());
            }

            // Call StripeService with converted amount and metadata.
            PaymentIntent pi = stripeService.createPaymentIntent(
                    dto.getAmount().longValue(), 
                    dto.getCurrency(),
                    dto.getDescription(),
                    dto.getUserId().toString(),
                    metadataMap
            );

            // Update payment record with Stripe payment intent reference.
            payment.setPaymentProviderReference(pi.getId());
            paymentRepository.save(payment);

            PaymentResponseDTO response = modelMapper.map(payment, PaymentResponseDTO.class);
            response.setClientSecret(pi.getClientSecret());
            response.setPaymentIntentId(pi.getId());
            response.setRequiresAction("requires_action".equals(pi.getStatus()) ||
                                       "requires_confirmation".equals(pi.getStatus()));

            log.info("Payment processed successfully. Payment ID: {}", payment.getPaymentId());
            return response;
        } catch (Exception e) {
            log.error("Error processing payment", e);
            throw new PaymentException("Error processing payment: " + e.getMessage());
        }
    }

    /**
     * Create payment setup.
     * Uses userId as customer identifier and converts metadata accordingly.
     */
    @Transactional
    public PaymentResponseDTO createPaymentSetup(PaymentRequestDTO dto) {
        log.info("Creating payment setup for user ID: {}", dto.getUserId());
        try {
            Map<String, String> metadataMap = new HashMap<>();
            if (dto.getMetadata() != null && !dto.getMetadata().isEmpty()) {
                metadataMap.put("metadata", dto.getMetadata());
            }
            String setupIntentId = stripeService.createSetupIntent(
                    dto.getUserId().toString(), metadataMap
            );

            PaymentResponseDTO response = new PaymentResponseDTO();
            response.setUserId(dto.getUserId());
            response.setSetupIntentId(setupIntentId);
            log.info("Payment setup created successfully");
            return response;
        } catch (Exception e) {
            log.error("Error creating payment setup", e);
            throw new PaymentException("Error creating payment setup: " + e.getMessage());
        }
    }

    /**
     * Process a refund.
     * Converts refund amount from BigDecimal to long and metadata to a Map.
     */
    @Transactional
    public PaymentResponseDTO processRefund(RefundRequestDTO dto) {
        log.info("Processing refund for payment ID: {}, amount: {}", dto.getPaymentId(), dto.getAmount());
        try {
            Payment payment = paymentRepository.findByPaymentId(dto.getPaymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + dto.getPaymentId()));
            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                throw new PaymentException("Payment cannot be refunded because it is not completed");
            }

            Map<String, String> metadataMap = new HashMap<>();
            if (dto.getMetadata() != null && !dto.getMetadata().isEmpty()) {
                metadataMap.put("metadata", dto.getMetadata());
            }

            Refund refund = stripeService.createRefund(
                    payment.getPaymentProviderReference(),
                    dto.getAmount().longValue(),
                    dto.getReason(),
                    metadataMap
            );
            payment.markAsRefunded(dto.getAmount(), dto.getReason(), refund.getId());
            paymentRepository.save(payment);

            PaymentResponseDTO response = modelMapper.map(payment, PaymentResponseDTO.class);
            log.info("Refund processed successfully. Payment ID: {}, Refund ID: {}", payment.getPaymentId(), refund.getId());
            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing refund", e);
            throw new PaymentException("Error processing refund: " + e.getMessage());
        }
    }

    public PaymentResponseDTO getPaymentById(String id) {
        log.info("Getting payment by ID: {}", id);
        Payment payment = paymentRepository.findByPaymentId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return modelMapper.map(payment, PaymentResponseDTO.class);
    }

    public Page<PaymentResponseDTO> getPaymentsByUserId(Long userId, Pageable pageable) {
        log.info("Getting payments for user ID: {}", userId);
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        return payments.map(p -> modelMapper.map(p, PaymentResponseDTO.class));
    }

    public Page<PaymentResponseDTO> getPaymentsByMerchantId(Long merchantId, Pageable pageable) {
        log.info("Getting payments for merchant ID: {}", merchantId);
        Page<Payment> payments = paymentRepository.findByMerchantId(merchantId, pageable);
        return payments.map(p -> modelMapper.map(p, PaymentResponseDTO.class));
    }

    public Page<PaymentResponseDTO> getPaymentsByOwnerId(Long ownerId, Pageable pageable) {
        log.info("Getting payments for owner ID: {}", ownerId);
        Page<Payment> payments = paymentRepository.findByOwnerId(ownerId, pageable);
        return payments.map(p -> modelMapper.map(p, PaymentResponseDTO.class));
    }

    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        log.info("Getting all payments");
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(p -> modelMapper.map(p, PaymentResponseDTO.class));
    }

    private Payment createPaymentRecord(PaymentRequestDTO dto) {
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setUserId(dto.getUserId());
        payment.setDescription(dto.getDescription());
        payment.setAmount(dto.getAmount());
        payment.setTax(dto.getTax());
        payment.setCurrency(dto.getCurrency().toLowerCase());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderReference(dto.getOrderReference());
        payment.setProductId(dto.getProductId());
        payment.setSubscriptionId(dto.getSubscriptionId());
        payment.setMerchantId(dto.getMerchantId());
        payment.setOwnerId(dto.getOwnerId());
        payment.setMetadata(dto.getMetadata());
        payment.setCustomerName(dto.getCustomerName());
        payment.setCustomerEmail(dto.getCustomerEmail());
        payment.setBillingAddress(dto.getBillingAddress());
        payment.setShippingAddress(dto.getShippingAddress());
        return paymentRepository.save(payment);
    }

    @Transactional
    public void updatePaymentStatus(String paymentIntentId, PaymentStatus status, String failureMessage, String failureCode) {
        log.info("Updating payment status for payment intent ID: {}, status: {}", paymentIntentId, status);
        Payment payment = paymentRepository.findByPaymentProviderReference(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with payment intent ID: " + paymentIntentId));
        if (status == PaymentStatus.COMPLETED) {
            payment.markAsCompleted();
        } else if (status == PaymentStatus.FAILED) {
            payment.markAsFailed(failureMessage, failureCode);
        } else {
            payment.setStatus(status);
        }
        paymentRepository.save(payment);
        log.info("Payment status updated successfully. Payment ID: {}", payment.getPaymentId());
    }

    public BigDecimal calculateTotalCompletedAmountForUser(Long userId) {
        BigDecimal total = paymentRepository.calculateTotalCompletedAmountForUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalCompletedAmountForMerchant(Long merchantId) {
        BigDecimal total = paymentRepository.calculateTotalCompletedAmountForMerchant(merchantId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalProcessedAmount(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating total processed amount between {} and {}", startDate, endDate);
        BigDecimal total = BigDecimal.ZERO;
        try {
            Page<Payment> payments = paymentRepository.findByCreatedAtBetween(startDate, endDate, Pageable.unpaged());
            total = payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            log.error("Error calculating total processed amount", e);
        }
        return total;
    }
}
