package com.demoApp.owner.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.demoApp.owner.dto.MenuItemDTO;
import com.demoApp.owner.entity.MenuItem;
import com.demoApp.owner.entity.Owner;
import com.demoApp.owner.entity.MenuItem.MenuType;
import com.demoApp.owner.exception.ResourceNotFoundException;
import com.demoApp.owner.repository.MenuItemRepository;
import com.demoApp.owner.repository.OwnerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return modelMapper.map(menuItem, MenuItemDTO.class);
    }

    public List<MenuItemDTO> getMenuItemsByOwner(Long ownerId) {
        if (!ownerRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
        }
        return menuItemRepository.findByOwnerId(ownerId).stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO) {
        Owner owner = ownerRepository.findById(menuItemDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + menuItemDTO.getOwnerId()));

        MenuItem menuItem = modelMapper.map(menuItemDTO, MenuItem.class);
        menuItem.setOwner(owner);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return modelMapper.map(savedMenuItem, MenuItemDTO.class);
    }

    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        // Update fields
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        // Corrected: Update price using setPrice, not setName
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setMenuType(menuItemDTO.getMenuType());

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return modelMapper.map(updatedMenuItem, MenuItemDTO.class);
    }

    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    public MenuItemDTO toggleAvailability(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        menuItem.setAvailable(!menuItem.isAvailable());
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return modelMapper.map(updatedMenuItem, MenuItemDTO.class);
    }

    public List<MenuItemDTO> getMenuItemsByCategory(Long ownerId, String category) {
        return menuItemRepository.findByOwnerIdAndCategory(ownerId, category).stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    public List<MenuItemDTO> getMenuItemsByType(Long ownerId, MenuType menuType) {
        return menuItemRepository.findByOwnerIdAndMenuType(ownerId, menuType).stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }

    public List<MenuItemDTO> getAvailableMenuItems(Long ownerId) {
        return menuItemRepository.findByOwnerIdAndAvailable(ownerId, true).stream()
                .map(item -> modelMapper.map(item, MenuItemDTO.class))
                .collect(Collectors.toList());
    }
}
