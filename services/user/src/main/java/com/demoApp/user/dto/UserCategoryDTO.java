package com.demoApp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;  
import jakarta.validation.constraints.NotEmpty;  
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryDTO {
    @NotBlank  
    private String name;
    
    @NotEmpty 
    private List<String> categories;
}
