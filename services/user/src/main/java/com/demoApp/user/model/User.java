package com.demoApp.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    private String name;
    private String description;
    private String imagePath;
    
    @Enumerated(EnumType.STRING)
    private UserType memberType;
    
    private boolean isVerified;
    private String address;
    private String mobileNumber;
    private String role;
    private String category;
    
    @ElementCollection
    private List<String> preferredCategory;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    
    public enum UserType {
        CUSTOMER, VENDOR, ADMIN
    }
}