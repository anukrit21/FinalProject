package com.demoApp.owner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "owners")
public class Owner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    
    @Column(nullable = false)
    private String password;
    
    private boolean isVerified;
    
    @Column(nullable = false)
    private double rating;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private String role = "ROLE_OWNER";

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
