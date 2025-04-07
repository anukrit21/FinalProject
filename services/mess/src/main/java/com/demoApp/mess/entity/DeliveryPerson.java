package com.demoApp.mess.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_persons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String vehicleType;
    private String vehicleNumber;
    private String idType;
    private String idNumber;
    private String profileImageUrl;
    private String idProofImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mess_id")
    private User mess;

    private boolean active;
    private Double averageRating;
    private Integer totalRatings;

    private Long createdBy;
    private Long updatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setIdProofUrl(String fileName) {
        this.idProofImageUrl = fileName;
    }
    
}