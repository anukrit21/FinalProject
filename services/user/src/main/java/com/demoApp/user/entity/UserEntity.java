package com.demoApp.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    private String name;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private UserType memberType;
    
    private String rating;
    private boolean isVerified;
    private String address;
    private String mobileNumber;
    private String role;
    
    @ElementCollection
    private List<String> preferredCategory;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    public enum UserType {
        CUSTOMER, VENDOR, ADMIN
    }
}
