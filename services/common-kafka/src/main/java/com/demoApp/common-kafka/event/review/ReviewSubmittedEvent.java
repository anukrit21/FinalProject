package com.demoApp.kafka.event.review;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewSubmittedEvent extends BaseEvent {
    
    private UUID reviewId;
    private UUID userId;
    private UUID messId;
    private UUID orderId;
    private LocalDateTime submittedAt;
    private BigDecimal rating;
    private String reviewText;
    private String reviewTitle;
    private boolean anonymous;
    private boolean verified;
    private String status; // PENDING, APPROVED, REJECTED
    private List<String> tags;
    private List<ReviewMedia> media;
    
    /**
     * Constructor with initialization
     */
    public ReviewSubmittedEvent(UUID reviewId, UUID userId, UUID messId, UUID orderId,
                              LocalDateTime submittedAt, BigDecimal rating, String reviewText,
                              String reviewTitle, boolean anonymous, boolean verified,
                              String status, List<String> tags, List<ReviewMedia> media) {
        super();
        init("REVIEW_SUBMITTED", "review-service");
        this.reviewId = reviewId;
        this.userId = userId;
        this.messId = messId;
        this.orderId = orderId;
        this.submittedAt = submittedAt;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewTitle = reviewTitle;
        this.anonymous = anonymous;
        this.verified = verified;
        this.status = status;
        this.tags = tags;
        this.media = media;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewMedia {
        private UUID mediaId;
        private String mediaType; // IMAGE, VIDEO
        private String mediaUrl;
        private String caption;
    }
} 