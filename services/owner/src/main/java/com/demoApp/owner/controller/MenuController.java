package com.demoApp.owner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.demoApp.owner.dto.MenuItemDTO;
import com.demoApp.owner.entity.MenuItem;
import com.demoApp.owner.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(menuService.getMenuItemsByOwner(ownerId));
    }

    @GetMapping("/owner/{ownerId}/category/{category}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByCategory(
            @PathVariable Long ownerId, @PathVariable String category) {
        return ResponseEntity.ok(menuService.getMenuItemsByCategory(ownerId, category));
    }

    @GetMapping("/owner/{ownerId}/type/{menuType}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByType(
            @PathVariable Long ownerId, @PathVariable MenuItem.MenuType menuType) {
        return ResponseEntity.ok(menuService.getMenuItemsByType(ownerId, menuType));
    }

    @GetMapping("/owner/{ownerId}/available")
    public ResponseEntity<List<MenuItemDTO>> getAvailableMenuItems(@PathVariable Long ownerId) {
        return ResponseEntity.ok(menuService.getAvailableMenuItems(ownerId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<MenuItemDTO> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO) {
        return new ResponseEntity<>(menuService.createMenuItem(menuItemDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Long id, @Valid @RequestBody MenuItemDTO menuItemDTO) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, menuItemDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-availability")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<MenuItemDTO> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.toggleAvailability(id));
    }
}