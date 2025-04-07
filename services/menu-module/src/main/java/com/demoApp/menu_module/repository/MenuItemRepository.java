package com.demoApp.menu_module.repository;

import com.demoApp.menu_module.entity.MenuItem;
import com.demoApp.menu_module.entity.MenuItemType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByMenuIdOrderByDisplayOrderAsc(Long menuId);
    List<MenuItem> findByMenuIdAndIsVegetarian(Long menuId, boolean isVegetarian);

    List<MenuItem> findByMenuIdAndCategory(Long menuId, MenuItemType category);
    
    @Query("SELECT mi FROM MenuItem mi WHERE mi.menu.owner.id = :ownerId AND mi.available = :available")
    List<MenuItem> findByMenu_Owner_IdAndAvailable(Long ownerId, boolean available);
    
    @Query("SELECT DISTINCT mi.category FROM MenuItem mi WHERE mi.menu.owner.id = :ownerId")
    List<String> findDistinctCategoriesByMenu_Owner_Id(Long ownerId);
    
    @Query("SELECT mi FROM MenuItem mi WHERE mi.menu.owner.id = :ownerId AND mi.isVegetarian = true")
    List<MenuItem> findByMenu_Owner_IdAndIsVegetarianTrue(Long ownerId);
    
    @Query("SELECT mi FROM MenuItem mi WHERE mi.menuId = :menuId")
    List<MenuItem> findByMenuId(Long menuId);
}