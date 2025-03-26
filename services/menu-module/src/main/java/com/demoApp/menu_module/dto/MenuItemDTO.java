package com.demoApp.menu_module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {
    
    private Long id;
    
    @NotBlank(message = "Item name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;
    
    private String imageUrl;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private Integer preparationTimeMinutes;
    
    private String nutritionalInfo;
    
    private String ingredients;
    
    private String dietaryRestrictions;
    
    @Builder.Default
    private Boolean isVegetarian = false;
    
    @Builder.Default
    private Boolean isVegan = false;
    
    @Builder.Default
    private Boolean isGlutenFree = false;
    
    @Builder.Default
    private Boolean isSpicy = false;
    
    @Builder.Default
    private Boolean available = true;
    
    @Builder.Default
    private Integer displayOrder = 0;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private Long menuId;
}
