package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.demoApp.mess.dto.MenuDTO;
import com.demoApp.mess.entity.Menu;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.MenuRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;

    public List<Menu> getAllMenuItems() {
        return menuRepository.findAll();
    }

    public List<Menu> getMenuItemsByMessId(Long messId) {
        return menuRepository.findByMessId(messId);
    }

    public Menu getMenuItemById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    public Menu createMenuItem(MenuDTO menuDTO) {
        Menu menu = modelMapper.map(menuDTO, Menu.class);
        return menuRepository.save(menu);
    }

    public Menu updateMenuItem(Long id, MenuDTO menuDTO) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        modelMapper.map(menuDTO, menu);
        return menuRepository.save(menu);
    }

    public void deleteMenuItem(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        menuRepository.delete(menu);
    }

    public List<Menu> getMenuItemsByMealType(Long messId, Menu.MealType mealType) {
        return menuRepository.findByMessIdAndMealType(messId, mealType);
    }

    public List<Menu> getAvailableMenuItems(Long messId) {
        return menuRepository.findByMessIdAndAvailable(messId, true);
    }
}