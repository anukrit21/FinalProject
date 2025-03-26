package com.demoApp.menu_module.dto;

import com.demoApp.menu_module.entity.Menu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    
    private Long id;
    
    @NotBlank(message = "Menu name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    private Long messId;
    
    @NotNull(message = "Menu type is required")
    private Menu.MenuType menuType;
    
    @Builder.Default
    private boolean active = true;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Builder.Default
    private Set<MenuItemDTO> menuItems = new HashSet<>();
}
