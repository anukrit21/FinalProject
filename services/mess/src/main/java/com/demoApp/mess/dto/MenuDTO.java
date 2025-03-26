package com.demoApp.mess.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.demoApp.mess.entity.Menu;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Meal type is required")
    private Menu.MealType mealType;
    
    private boolean available = true;
    
    @NotNull(message = "Mess ID is required")
    private Long messId;
}
