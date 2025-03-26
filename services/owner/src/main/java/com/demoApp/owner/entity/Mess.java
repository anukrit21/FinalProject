package com.demoApp.owner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "mess")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Builder.Default
    private boolean available = true;
    
    @Column(precision = 2, scale = 1)
    private Float rating;
    
    @ElementCollection
    private List<String> menu;
}
