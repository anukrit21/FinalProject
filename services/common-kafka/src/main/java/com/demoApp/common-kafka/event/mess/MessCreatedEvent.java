package com.demoApp.kafka.event.mess;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessCreatedEvent extends BaseEvent {
    
    private UUID messId;
    private UUID ownerId;
    private String name;
    private String description;
    private String phoneNumber;
    private String emailAddress;
    private BigDecimal rating;
    private boolean verified;
    private boolean active;
    private List<MessAddress> addresses;
    private List<OperatingHours> operatingHours;
    private List<String> cuisineTypes;
    private List<String> amenities;
    private MessPricingInfo pricingInfo;
    
    /**
     * Constructor with initialization
     */
    public MessCreatedEvent(UUID messId, UUID ownerId, String name, String description,
                          String phoneNumber, String emailAddress, BigDecimal rating,
                          boolean verified, boolean active, List<MessAddress> addresses,
                          List<OperatingHours> operatingHours, List<String> cuisineTypes,
                          List<String> amenities, MessPricingInfo pricingInfo) {
        super();
        init("MESS_CREATED", "mess-service");
        this.messId = messId;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.rating = rating;
        this.verified = verified;
        this.active = active;
        this.addresses = addresses;
        this.operatingHours = operatingHours;
        this.cuisineTypes = cuisineTypes;
        this.amenities = amenities;
        this.pricingInfo = pricingInfo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessAddress {
        private UUID addressId;
        private String type; // PRIMARY, BRANCH
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String landmark;
        private Double latitude;
        private Double longitude;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatingHours {
        private String dayOfWeek; // MONDAY, TUESDAY, etc.
        private LocalTime breakfastStart;
        private LocalTime breakfastEnd;
        private LocalTime lunchStart;
        private LocalTime lunchEnd;
        private LocalTime dinnerStart;
        private LocalTime dinnerEnd;
        private boolean closed;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessPricingInfo {
        private BigDecimal breakfastPrice;
        private BigDecimal lunchPrice;
        private BigDecimal dinnerPrice;
        private BigDecimal monthlySubscriptionPrice;
    }
} 