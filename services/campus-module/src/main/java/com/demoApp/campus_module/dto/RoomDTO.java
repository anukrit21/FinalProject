package com.demoApp.campus_module.dto;

import com.demoApp.campus_module.entity.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    
    private Long id;

    @NotBlank(message = "Room name is required")
    private String name;

    private String description;

    @NotBlank(message = "Room number is required")
    private String number;

    private Integer floor;

    private Integer capacity;

    private Double area;

    @NotNull(message = "Room type is required")
    private Room.RoomType roomType;

    @Builder.Default
    private Room.RoomStatus status = Room.RoomStatus.AVAILABLE;  // 👈 Fix: Ensures default value

    @Builder.Default
    private Boolean active = true;  // 👈 Fix: Ensures 'true' is used by default

    @NotNull(message = "Building ID is required")
    private Long buildingId;

    private String buildingName;

    private String buildingCode;

    private Long campusId;

    private String campusName;

    private Long messId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
