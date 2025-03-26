package com.demoApp.campus_module.dto;

import com.demoApp.campus_module.entity.Building;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildingDTO {
    
    private Long id;

    @NotBlank(message = "Building name is required")
    private String name;

    private String description;

    @NotBlank(message = "Building code is required")
    private String code;

    private String address;

    private Integer floors;

    private Double latitude;

    private Double longitude;

    @NotNull(message = "Building type is required")
    private Building.BuildingType buildingType;

    @Builder.Default
    private Boolean active = true;  // 

    @NotNull(message = "Campus ID is required")
    private Long campusId;

    private String campusName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private Set<RoomDTO> rooms = new HashSet<>(); 
}
