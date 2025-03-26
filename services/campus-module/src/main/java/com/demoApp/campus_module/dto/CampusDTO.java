package com.demoApp.campus_module.dto;

import com.demoApp.campus_module.entity.Campus;
import jakarta.validation.constraints.Email;
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
public class CampusDTO {
    
    private Long id;

    @NotBlank(message = "Campus name is required")
    private String name;

    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;
    
    private String state;

    private String country;

    private String postalCode;

    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String email;

    private String website;

    @NotNull(message = "Campus type is required")
    private Campus.CampusType campusType;

    @Builder.Default
    private Boolean active = true;  // 👈 Fix: Ensures 'true' is used by default

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private Set<BuildingDTO> buildings = new HashSet<>();  // 👈 Fix: Prevents `null` Set
}
