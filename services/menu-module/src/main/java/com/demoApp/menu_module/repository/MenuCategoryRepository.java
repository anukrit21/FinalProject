package com.demoApp.menu_module.repository;

import com.demoApp.menu_module.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findByOwnerId(Long ownerId);
    
    List<MenuCategory> findByOwnerIdAndActive(Long ownerId, boolean active);
    
    @Query("SELECT mc FROM MenuCategory mc WHERE mc.ownerId = :ownerId ORDER BY mc.displayOrder ASC")
    List<MenuCategory> findByOwnerIdOrderByDisplayOrderAsc(Long ownerId);
    
    @Query("SELECT mc FROM MenuCategory mc WHERE mc.ownerId = :ownerId AND mc.active = true ORDER BY mc.displayOrder ASC")
    List<MenuCategory> findActiveByOwnerIdOrderByDisplayOrderAsc(Long ownerId);
    
    boolean existsByOwnerIdAndName(Long ownerId, String name);
} 