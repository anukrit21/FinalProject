package com.demoApp.menu_module.service;

import com.demoApp.menu_module.dto.MenuCategoryDTO;
import com.demoApp.menu_module.entity.MenuCategory;
import com.demoApp.menu_module.exception.ResourceAlreadyExistsException;
import com.demoApp.menu_module.exception.ResourceNotFoundException;
import com.demoApp.menu_module.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = "categories", key = "'all'")
    public List<MenuCategoryDTO> getAllCategories() {
        log.info("Fetching all menu categories");
        return menuCategoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, MenuCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "#id")
    public MenuCategoryDTO getCategoryById(Long id) {
        log.info("Fetching menu category with id: {}", id);
        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found with id: " + id));
        return modelMapper.map(category, MenuCategoryDTO.class);
    }

    @Cacheable(value = "categories", key = "'owner_' + #ownerId")
    public List<MenuCategoryDTO> getCategoriesByOwner(Long ownerId) {
        log.info("Fetching menu categories for owner with id: {}", ownerId);
        return menuCategoryRepository.findByOwnerId(ownerId).stream()
                .map(category -> modelMapper.map(category, MenuCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "'owner_' + #ownerId + '_active_' + #active")
    public List<MenuCategoryDTO> getCategoriesByOwnerAndActive(Long ownerId, boolean active) {
        log.info("Fetching {} menu categories for owner with id: {}", active ? "active" : "inactive", ownerId);
        return menuCategoryRepository.findByOwnerIdAndActive(ownerId, active).stream()
                .map(category -> modelMapper.map(category, MenuCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "'owner_' + #ownerId + '_ordered'")
    public List<MenuCategoryDTO> getCategoriesByOwnerOrdered(Long ownerId) {
        log.info("Fetching ordered menu categories for owner with id: {}", ownerId);
        return menuCategoryRepository.findByOwnerIdOrderByDisplayOrderAsc(ownerId).stream()
                .map(category -> modelMapper.map(category, MenuCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "'owner_' + #ownerId + '_active_ordered'")
    public List<MenuCategoryDTO> getActiveCategoriesByOwnerOrdered(Long ownerId) {
        log.info("Fetching ordered active menu categories for owner with id: {}", ownerId);
        return menuCategoryRepository.findActiveByOwnerIdOrderByDisplayOrderAsc(ownerId).stream()
                .map(category -> modelMapper.map(category, MenuCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public MenuCategoryDTO createCategory(MenuCategoryDTO categoryDTO) {
        log.info("Creating new menu category: {}", categoryDTO.getName());
        
        // Check if category already exists with the same name for this owner
        if (menuCategoryRepository.existsByOwnerIdAndName(categoryDTO.getOwnerId(), categoryDTO.getName())) {
            throw new ResourceAlreadyExistsException("Category with name '" + categoryDTO.getName() + 
                    "' already exists for this owner");
        }
        
        MenuCategory category = modelMapper.map(categoryDTO, MenuCategory.class);
        MenuCategory savedCategory = menuCategoryRepository.save(category);
        return modelMapper.map(savedCategory, MenuCategoryDTO.class);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public MenuCategoryDTO updateCategory(Long id, MenuCategoryDTO categoryDTO) {
        log.info("Updating menu category with id: {}", id);
        
        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found with id: " + id));
        
        // Check if updating to a name that already exists for this owner
        if (!category.getName().equals(categoryDTO.getName()) && 
                menuCategoryRepository.existsByOwnerIdAndName(category.getOwnerId(), categoryDTO.getName())) {
            throw new ResourceAlreadyExistsException("Category with name '" + categoryDTO.getName() + 
                    "' already exists for this owner");
        }
        
        // Update fields
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setImageUrl(categoryDTO.getImageUrl());
        category.setDisplayOrder(categoryDTO.getDisplayOrder());
        category.setActive(categoryDTO.getActive());
        
        MenuCategory updatedCategory = menuCategoryRepository.save(category);
        return modelMapper.map(updatedCategory, MenuCategoryDTO.class);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        log.info("Deleting menu category with id: {}", id);
        if (!menuCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu category not found with id: " + id);
        }
        menuCategoryRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = "categories", key = "#id")
    public MenuCategoryDTO toggleCategoryActive(Long id) {
        log.info("Toggling active status for menu category with id: {}", id);
        
        MenuCategory category = menuCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu category not found with id: " + id));
        
        category.setActive(!category.getActive());
        MenuCategory updatedCategory = menuCategoryRepository.save(category);
        return modelMapper.map(updatedCategory, MenuCategoryDTO.class);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public List<MenuCategoryDTO> updateCategoriesOrder(Long ownerId, List<Long> categoryIds) {
        log.info("Updating display order for menu categories for owner with id: {}", ownerId);
        
        for (int i = 0; i < categoryIds.size(); i++) {
            MenuCategory category = menuCategoryRepository.findById(categoryIds.get(i))
                    .orElseThrow(() -> new ResourceNotFoundException("Menu category not found"));
            
            if (!category.getOwnerId().equals(ownerId)) {
                throw new IllegalArgumentException("Menu category does not belong to the specified owner");
            }
            
            category.setDisplayOrder(i);
            menuCategoryRepository.save(category);
        }
        
        return menuCategoryRepository.findByOwnerIdOrderByDisplayOrderAsc(ownerId).stream()
                .map(category -> modelMapper.map(category, MenuCategoryDTO.class))
                .collect(Collectors.toList());
    }
} 