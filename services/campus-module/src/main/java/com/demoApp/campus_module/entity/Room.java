package com.demoApp.campus_module.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    
    @Column(nullable = false)
    private String number;
    
    private Integer floor;
    
    private Integer capacity;
    
    private Double area;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.AVAILABLE;  // ✅ Fix: Ensures 'AVAILABLE' is used as default when building
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;  // ✅ Fix: Ensures 'true' is used as default when building
    
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
    
    // Field to store the associated mess ID if this room is a dining hall
    private Long messId;
    
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // ✅ Fix: Ensures timestamp is set when building

    private LocalDateTime updatedAt;
    
    public enum RoomType {
        CLASSROOM, OFFICE, LABORATORY, CONFERENCE, DINING_HALL, DORMITORY, STUDY_ROOM, RECREATION, OTHER
    }
    
    public enum RoomStatus {
        AVAILABLE, OCCUPIED, UNDER_MAINTENANCE, RESERVED, CLOSED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
