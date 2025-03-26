package com.demoApp.mess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demoApp.mess.entity.Menu;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByMessId(Long messId);
    List<Menu> findByMessIdAndMealType(Long messId, Menu.MealType mealType);
    List<Menu> findByMessIdAndAvailable(Long messId, boolean available);
}