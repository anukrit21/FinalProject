package com.demoApp.menu_module.repository;

import com.demoApp.menu_module.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByOwnerId(Long ownerId);
    
    List<Menu> findByOwnerIdAndActive(Long ownerId, boolean active);
    
    List<Menu> findByOwnerIdAndMenuType(Long ownerId, Menu.MenuType menuType);
    
    List<Menu> findByMessId(Long messId);
    
    List<Menu> findByMessIdAndActive(Long messId, boolean active);
    
    @Query("SELECT m FROM Menu m WHERE m.ownerId = :ownerId ORDER BY m.createdAt DESC")
    List<Menu> findLatestMenusByOwner(Long ownerId);
    
    @Query("SELECT DISTINCT m.menuType FROM Menu m WHERE m.ownerId = :ownerId")
    List<Menu.MenuType> findDistinctMenuTypesByOwnerId(Long ownerId);
} 