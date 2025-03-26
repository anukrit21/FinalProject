package com.demoApp.menu_module.controller;

import com.demoApp.menu_module.dto.ApiResponse;
import com.demoApp.menu_module.dto.MenuItemDTO;
import com.demoApp.menu_module.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllMenuItems() {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getAllMenuItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemById(id)));
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<ApiResponse<Object>> getMenuItemsByMenu(@PathVariable Long menuId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemById(menuId)));
    }

    @GetMapping("/menu/{menuId}/ordered")
    public ResponseEntity<ApiResponse<Object>> getMenuItemsByMenuOrdered(@PathVariable Long menuId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemsByMenuOrdered(menuId)));
    }

    @GetMapping("/menu/{menuId}/available")
    public ResponseEntity<ApiResponse<Object>> getAvailableMenuItemsByMenu(
            @PathVariable Long menuId, @RequestParam(defaultValue = "true") boolean available) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemsByMenuAndCategory(menuId, available)));
    }

    @GetMapping("/menu/{menuId}/category/{category}")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getMenuItemsByMenuAndCategory(
            @PathVariable Long menuId, @PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemsByMenuAndCategory(menuId, category)));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<Object>> getMenuItemsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemById(ownerId)));
    }

    @GetMapping("/owner/{ownerId}/available")
    public ResponseEntity<ApiResponse<Object>> getAvailableMenuItemsByOwner(
            @PathVariable Long ownerId, @RequestParam(defaultValue = "true") boolean available) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemsByOwnerAndAvailability(ownerId, available)));
    }

    @GetMapping("/owner/{ownerId}/categories")
    public ResponseEntity<ApiResponse<Object>> getCategoriesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getCategoriesByOwner(ownerId)));
    }

    @GetMapping("/owner/{ownerId}/vegetarian")
    public ResponseEntity<ApiResponse<Object>> getVegetarianItemsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getVegetarianItemsByOwner(ownerId)));
    }

    @GetMapping("/owner/{ownerId}/vegan")
    public ResponseEntity<ApiResponse<Object>> getVeganItemsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getVegetarianItemsByOwner(ownerId)));
    }

    // Public endpoints that don't require authentication
    @GetMapping("/public/menu/{menuId}")
    public ResponseEntity<ApiResponse<Object>> getPublicMenuItemsByMenu(@PathVariable Long menuId) {
        return ResponseEntity.ok(ApiResponse.success(menuItemService.getMenuItemsByMenuAndCategory(menuId, true)));
    }

    @GetMapping("/public/menu/{menuId}/category/{category}")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getPublicMenuItemsByMenuAndCategory(
            @PathVariable Long menuId, @PathVariable String category) {
        List<MenuItemDTO> items = menuItemService.getMenuItemsByMenuAndCategory(menuId, category).stream()
                .filter(MenuItemDTO::getAvailable)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemDTO>> createMenuItem(@Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO createdItem = menuItemService.createMenuItem(menuItemDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Menu item created successfully", createdItem),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemDTO>> updateMenuItem(
            @PathVariable Long id, @Valid @RequestBody MenuItemDTO menuItemDTO) {
        List<Long> itemIds = null;
                return ResponseEntity.ok(
                        ApiResponse.success("Menu item updated successfully", menuItemService.updateMenuItem(id, itemIds)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok(ApiResponse.success("Menu item deleted successfully", null));
    }

    @PatchMapping("/{id}/toggle-availability")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Object>> toggleMenuItemAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Menu item availability toggled", menuItemService.toggleAvailability(id)));
    }

    @PatchMapping("/menu/{menuId}/order")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemDTO>> updateMenuItemsOrder(
            @PathVariable Long menuId, @RequestBody List<Long> itemIds) {
        return ResponseEntity.ok(
                ApiResponse.success("Menu items order updated", menuItemService.updateMenuItem(menuId, itemIds)));
    }
} 