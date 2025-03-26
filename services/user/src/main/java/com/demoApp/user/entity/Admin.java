package com.demoApp.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserEntity.UserType memberType = UserEntity.UserType.ADMIN;
    
    private String role;
    private boolean enabled;
    
    @Column(name = "category_id")
    private Long categoryId;
}
