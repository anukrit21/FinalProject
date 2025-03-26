package com.demoApp.menu_module.controller;

import com.demoApp.menu_module.dto.ApiResponse;
import com.demoApp.menu_module.dto.MenuDTO;
import com.demoApp.menu_module.entity.Menu;
import com.demoApp.menu_module.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getAllMenus() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getAllMenus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuDTO>> getMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuById(id)));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenusByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByOwner(ownerId)));
    }

    @GetMapping("/owner/{ownerId}/active")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getActiveMenusByOwner(
            @PathVariable Long ownerId, @RequestParam(defaultValue = "true") boolean active) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByOwnerAndActive(ownerId, active)));
    }

    @GetMapping("/owner/{ownerId}/type/{menuType}")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenusByOwnerAndType(
            @PathVariable Long ownerId, @PathVariable Menu.MenuType menuType) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByOwnerAndType(ownerId, menuType)));
    }

    @GetMapping("/mess/{messId}")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenusByMess(@PathVariable Long messId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByMess(messId)));
    }

    @GetMapping("/owner/{ownerId}/latest")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getLatestMenusByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getLatestMenusByOwner(ownerId)));
    }

    @GetMapping("/owner/{ownerId}/types")
    public ResponseEntity<ApiResponse<List<Menu.MenuType>>> getMenuTypesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuTypesByOwner(ownerId)));
    }

    // Public endpoints that don't require authentication
    @GetMapping("/public/mess/{messId}")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getPublicMenusByMess(@PathVariable Long messId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByMessAndActive(messId, true)));
    }

    @GetMapping("/public/mess/{messId}/active")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getPublicActiveMenusByMess(@PathVariable Long messId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByMessAndActive(messId, true)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuDTO>> createMenu(@Valid @RequestBody MenuDTO menuDTO) {
        MenuDTO createdMenu = menuService.createMenu(menuDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Menu created successfully", createdMenu),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuDTO>> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuDTO menuDTO) {
        return ResponseEntity.ok(ApiResponse.success("Menu updated successfully", menuService.updateMenu(id, menuDTO)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok(ApiResponse.success("Menu deleted successfully", null));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuDTO>> toggleMenuActive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Menu status toggled", menuService.toggleMenuActive(id)));
    }
} 