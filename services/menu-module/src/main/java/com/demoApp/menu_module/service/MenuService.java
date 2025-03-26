package com.demoApp.menu_module.service;

import com.demoApp.menu_module.dto.MenuDTO;
import com.demoApp.menu_module.entity.Menu;
import com.demoApp.menu_module.entity.MenuItem;
import com.demoApp.menu_module.exception.ResourceNotFoundException;
import com.demoApp.menu_module.repository.MenuItemRepository;
import com.demoApp.menu_module.repository.MenuRepository;
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
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = "menus", key = "'all'")
    public List<MenuDTO> getAllMenus() {
        log.info("Fetching all menus");
        return menuRepository.findAll().stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menus", key = "#id")
    public MenuDTO getMenuById(Long id) {
        log.info("Fetching menu with id: {}", id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
        return modelMapper.map(menu, MenuDTO.class);
    }

    @Cacheable(value = "menus", key = "'owner_' + #ownerId")
    public List<MenuDTO> getMenusByOwner(Long ownerId) {
        log.info("Fetching menus for owner with id: {}", ownerId);
        return menuRepository.findByOwnerId(ownerId).stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menus", key = "'owner_' + #ownerId + '_active_' + #active")
    public List<MenuDTO> getMenusByOwnerAndActive(Long ownerId, boolean active) {
        log.info("Fetching {} menus for owner with id: {}", active ? "active" : "inactive", ownerId);
        return menuRepository.findByOwnerIdAndActive(ownerId, active).stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menus", key = "'owner_' + #ownerId + '_type_' + #menuType")
    public List<MenuDTO> getMenusByOwnerAndType(Long ownerId, Menu.MenuType menuType) {
        log.info("Fetching menus of type {} for owner with id: {}", menuType, ownerId);
        return menuRepository.findByOwnerIdAndMenuType(ownerId, menuType).stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menus", key = "'mess_' + #messId")
    public List<MenuDTO> getMenusByMess(Long messId) {
        log.info("Fetching menus for mess with id: {}", messId);
        return menuRepository.findByMessId(messId).stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menus", key = "'mess_' + #messId + '_active_' + #active")
    public List<MenuDTO> getMenusByMessAndActive(Long messId, boolean active) {
        log.info("Fetching {} menus for mess with id: {}", active ? "active" : "inactive", messId);
        return menuRepository.findByMessIdAndActive(messId, active).stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"menus", "menuItems"}, allEntries = true)
    public MenuDTO createMenu(MenuDTO menuDTO) {
        log.info("Creating new menu: {}", menuDTO.getName());
        Menu menu = modelMapper.map(menuDTO, Menu.class);
        Menu savedMenu = menuRepository.save(menu);
        return modelMapper.map(savedMenu, MenuDTO.class);
    }

    @Transactional
    @CacheEvict(value = {"menus", "menuItems"}, allEntries = true)
    public MenuDTO updateMenu(Long id, MenuDTO menuDTO) {
        log.info("Updating menu with id: {}", id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));

        // Update fields
        menu.setName(menuDTO.getName());
        menu.setDescription(menuDTO.getDescription());
        menu.setMenuType(menuDTO.getMenuType());
        menu.setActive(menuDTO.isActive());
        
        if (menuDTO.getMessId() != null) {
            menu.setMessId(menuDTO.getMessId());
        }

        Menu updatedMenu = menuRepository.save(menu);
        return modelMapper.map(updatedMenu, MenuDTO.class);
    }

    @Transactional
    @CacheEvict(value = {"menus", "menuItems"}, allEntries = true)
    public void deleteMenu(Long id) {
        log.info("Deleting menu with id: {}", id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
        
        // Delete all menu items associated with this menu
        List<MenuItem> menuItems = menuItemRepository.findByMenuId(id);
        menuItemRepository.deleteAll(menuItems);
        
        menuRepository.delete(menu);
    }

    @Transactional
    @CacheEvict(value = "menus", key = "#id")
    public MenuDTO toggleMenuActive(Long id) {
        log.info("Toggling active status for menu with id: {}", id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
        
        menu.setActive(!menu.isActive());
        Menu updatedMenu = menuRepository.save(menu);
        return modelMapper.map(updatedMenu, MenuDTO.class);
    }

    @Cacheable(value = "menus", key = "'latest_owner_' + #ownerId")
    public List<MenuDTO> getLatestMenusByOwner(Long ownerId) {
        log.info("Fetching latest menus for owner with id: {}", ownerId);
        return menuRepository.findLatestMenusByOwner(ownerId).stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "menus", key = "'types_owner_' + #ownerId")
    public List<Menu.MenuType> getMenuTypesByOwner(Long ownerId) {
        log.info("Fetching menu types for owner with id: {}", ownerId);
        return menuRepository.findDistinctMenuTypesByOwnerId(ownerId);
    }
} 