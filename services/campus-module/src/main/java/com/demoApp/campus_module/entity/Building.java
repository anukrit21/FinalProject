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
@Table(name = "buildings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    
    @Column(nullable = false)
    private String code;
    
    private String address;
    
    private Integer floors;
    
    private Double latitude;
    
    private Double longitude;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuildingType buildingType;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;  // 👈 Fix: Ensures 'true' is used as default

    @ManyToOne
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;
    
    @Builder.Default
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();  // 👈 Fix: Ensures an empty HashSet is created by default

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 👈 Fix: Ensures timestamp is set

    private LocalDateTime updatedAt;

    public enum BuildingType {
        ACADEMIC, ADMINISTRATIVE, RESIDENTIAL, DINING, RECREATIONAL, LABORATORY, LIBRARY, OTHER
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper method to add a room
    public void addRoom(Room room) {
        rooms.add(room);
        room.setBuilding(this);
    }
    
    // Helper method to remove a room
    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setBuilding(null);
    }
}
