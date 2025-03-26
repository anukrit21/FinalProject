package com.demoApp.menu_module.controller;

import com.demoApp.menu_module.dto.ApiResponse;
import com.demoApp.menu_module.dto.MenuCategoryDTO;
import com.demoApp.menu_module.service.MenuCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-categories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getAllCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuCategoryDTO>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getCategoryById(id)));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getCategoriesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getCategoriesByOwner(ownerId)));
    }

    @GetMapping("/owner/{ownerId}/active")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getActiveCategoriesByOwner(
            @PathVariable Long ownerId, @RequestParam(defaultValue = "true") boolean active) {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getCategoriesByOwnerAndActive(ownerId, active)));
    }

    @GetMapping("/owner/{ownerId}/ordered")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getCategoriesByOwnerOrdered(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getCategoriesByOwnerOrdered(ownerId)));
    }

    @GetMapping("/owner/{ownerId}/active-ordered")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getActiveCategoriesByOwnerOrdered(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getActiveCategoriesByOwnerOrdered(ownerId)));
    }

    // Public endpoints that don't require authentication
    @GetMapping("/public/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getPublicCategoriesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getCategoriesByOwnerAndActive(ownerId, true)));
    }

    @GetMapping("/public/owner/{ownerId}/ordered")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getPublicCategoriesByOwnerOrdered(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(menuCategoryService.getActiveCategoriesByOwnerOrdered(ownerId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuCategoryDTO>> createCategory(@Valid @RequestBody MenuCategoryDTO categoryDTO) {
        MenuCategoryDTO createdCategory = menuCategoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Category created successfully", createdCategory),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuCategoryDTO>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody MenuCategoryDTO categoryDTO) {
        return ResponseEntity.ok(
                ApiResponse.success("Category updated successfully", menuCategoryService.updateCategory(id, categoryDTO)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        menuCategoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuCategoryDTO>> toggleCategoryActive(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Category status toggled", menuCategoryService.toggleCategoryActive(id)));
    }

    @PatchMapping("/owner/{ownerId}/order")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> updateCategoriesOrder(
            @PathVariable Long ownerId, @RequestBody List<Long> categoryIds) {
        return ResponseEntity.ok(
                ApiResponse.success("Categories order updated", menuCategoryService.updateCategoriesOrder(ownerId, categoryIds)));
    }
} 