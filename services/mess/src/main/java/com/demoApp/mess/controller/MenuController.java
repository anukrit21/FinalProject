package com.demoApp.mess.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.demoApp.mess.dto.ApiResponse;
import com.demoApp.mess.dto.MenuDTO;
import com.demoApp.mess.entity.Menu;
import com.demoApp.mess.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<Menu>> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }

    @GetMapping("/mess/{messId}")
    public ResponseEntity<List<Menu>> getMenuItemsByMessId(@PathVariable Long messId) {
        return ResponseEntity.ok(menuService.getMenuItemsByMessId(messId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MESS')")
    public ResponseEntity<Menu> createMenuItem(@Valid @RequestBody MenuDTO menuDTO) {
        return new ResponseEntity<>(menuService.createMenuItem(menuDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MESS')")
    public ResponseEntity<Menu> updateMenuItem(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, menuDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MESS')")
    public ResponseEntity<ApiResponse> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(new ApiResponse(true, "Menu item deleted successfully"));
    }

    @GetMapping("/mess/{messId}/meal-type/{mealType}")
    public ResponseEntity<List<Menu>> getMenuItemsByMealType(
            @PathVariable Long messId,
            @PathVariable Menu.MealType mealType) {
        return ResponseEntity.ok(menuService.getMenuItemsByMealType(messId, mealType));
    }

    @GetMapping("/mess/{messId}/available")
    public ResponseEntity<List<Menu>> getAvailableMenuItems(@PathVariable Long messId) {
        return ResponseEntity.ok(menuService.getAvailableMenuItems(messId));
    }
}