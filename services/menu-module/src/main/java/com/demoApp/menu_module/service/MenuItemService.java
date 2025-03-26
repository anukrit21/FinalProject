package com.demoApp.menu_module.service;

import com.demoApp.menu_module.dto.MenuItemDTO;
import com.demoApp.menu_module.entity.Menu;
import com.demoApp.menu_module.entity.MenuItem;
import com.demoApp.menu_module.entity.MenuItemType;
import com.demoApp.menu_module.exception.ResourceNotFoundException;
import com.demoApp.menu_module.repository.MenuItemRepository;
import com.demoApp.menu_module.repository.MenuRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = "menuItems")
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menuItems", key = "#id")
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return modelMapper.map(menuItem, MenuItemDTO.class);
    }

    @Cacheable(value = "menuItems", key = "#menuId + '-ordered'")
    public List<MenuItemDTO> getMenuItemsByMenuOrdered(Long menuId) {
        // Convert the returned collection (Set or otherwise) into a List.
        return new ArrayList<>(menuItemRepository.findByMenuIdOrderByDisplayOrderAsc(menuId))
                .stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menuItems", key = "#menuId + '-' + #category")
    public List<MenuItemDTO> getMenuItemsByMenuAndCategory(Long menuId, String category) {
        // Convert the collection to a List to be able to use stream operations
        return new ArrayList<>(menuItemRepository.findByMenuIdAndCategory(menuId,
                MenuItemType.valueOf(category.toUpperCase())))
                .stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menuItems", key = "#ownerId + '-' + #available")
    public List<MenuItemDTO> getMenuItemsByOwnerAndAvailability(Long ownerId, boolean available) {
        return menuItemRepository.findByMenu_Owner_IdAndAvailable(ownerId, available)
                .stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "categories", key = "#ownerId")
    public List<String> getCategoriesByOwner(Long ownerId) {
        return menuItemRepository.findDistinctCategoriesByMenu_Owner_Id(ownerId);
    }

    @Cacheable(value = "menuItems", key = "#ownerId + '-vegetarian'")
    public List<MenuItemDTO> getVegetarianItemsByOwner(Long ownerId) {
        return menuItemRepository.findByMenu_Owner_IdAndIsVegetarianTrue(ownerId)
                .stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"menuItems"}, key = "#id")
    public MenuItemDTO toggleAvailability(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        menuItem.setAvailable(!menuItem.getAvailable());
        return modelMapper.map(menuItemRepository.save(menuItem), MenuItemDTO.class);
    }

    @Transactional
    @CacheEvict(value = {"menuItems", "categories"}, allEntries = true)
    public MenuItemDTO updateMenuItem(Long menuId, List<Long> itemIds) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + menuId));
        
        int order = 0;
        for (Long itemId : itemIds) {
            MenuItem menuItem = menuItemRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemId));
            menuItem.setMenu(menu);
            menuItem.setDisplayOrder(order++);
            menuItemRepository.save(menuItem);
        }
        // Convert the collection of menu items to a list and return the first item as a sample DTO
        List<MenuItem> items = new ArrayList<>(menu.getMenuItems());
        if (!items.isEmpty()) {
            return modelMapper.map(items.get(0), MenuItemDTO.class);
        } else {
            throw new ResourceNotFoundException("No menu items found for menu with id: " + menuId);
        }
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createMenuItem'");
    }

    public Object getMenuItemsByMenuAndCategory(Long menuId, boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMenuItemsByMenuAndCategory'");
    }
}
