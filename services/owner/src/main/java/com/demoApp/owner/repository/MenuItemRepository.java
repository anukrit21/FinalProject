package com.demoApp.owner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demoApp.owner.entity.MenuItem;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByOwnerId(Long ownerId);
    List<MenuItem> findByOwnerIdAndCategory(Long ownerId, String category);
    List<MenuItem> findByOwnerIdAndMenuType(Long ownerId, MenuItem.MenuType menuType);
    List<MenuItem> findByOwnerIdAndAvailable(Long ownerId, boolean available);
}