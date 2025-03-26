package com.demoApp.kafka.event.menu;

import com.demoApp.kafka.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MenuUpdatedEvent extends BaseEvent {
    
    private UUID menuId;
    private UUID messId;
    private LocalDate date;
    private String mealType; // BREAKFAST, LUNCH, DINNER
    private List<MenuItem> items;
    
    /**
     * Constructor with initialization
     */
    public MenuUpdatedEvent(UUID menuId, UUID messId, LocalDate date, String mealType, List<MenuItem> items) {
        super();
        init("MENU_UPDATED", "menu-service");
        this.menuId = menuId;
        this.messId = messId;
        this.date = date;
        this.mealType = mealType;
        this.items = items;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItem {
        private UUID itemId;
        private String name;
        private String description;
        private boolean vegetarian;
    }
} 