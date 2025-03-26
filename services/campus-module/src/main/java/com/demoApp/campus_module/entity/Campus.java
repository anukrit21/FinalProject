package com.demoApp.campus_module.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "campuses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String address;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String phoneNumber;

    private String email;

    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampusType campusType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;  // 👈 Fix: Ensures 'true' is used as default when building

    @Builder.Default
    @OneToMany(mappedBy = "campus", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Building> buildings = new HashSet<>();  // 👈 Fix: Ensures an empty HashSet is created by default

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 👈 Fix: Ensures timestamp is set

    private LocalDateTime updatedAt;
    
    public enum CampusType {
        UNIVERSITY, CORPORATE, GOVERNMENT, SCHOOL, HOSPITAL, OTHER
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to add a building
    public void addBuilding(Building building) {
        buildings.add(building);
        building.setCampus(this);
    }

    // Helper method to remove a building
    public void removeBuilding(Building building) {
        buildings.remove(building);
        building.setCampus(null);
    }
}
