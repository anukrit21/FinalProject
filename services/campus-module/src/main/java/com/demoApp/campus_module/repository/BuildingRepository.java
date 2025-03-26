package com.demoApp.campus_module.repository;

import com.demoApp.campus_module.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    List<Building> findByCampusId(Long campusId);
    
    List<Building> findByCampusIdAndActive(Long campusId, boolean active);
    
    List<Building> findByCampusIdAndBuildingType(Long campusId, Building.BuildingType buildingType);
    
    List<Building> findByBuildingType(Building.BuildingType buildingType);
    
    @Query("SELECT b FROM Building b WHERE b.campus.id = :campusId ORDER BY b.name ASC")
    List<Building> findByCampusIdOrderByNameAsc(Long campusId);
    
    @Query("SELECT DISTINCT b.buildingType FROM Building b WHERE b.campus.id = :campusId")
    List<Building.BuildingType> findDistinctBuildingTypesByCampusId(Long campusId);
    
    boolean existsByCampusIdAndCode(Long campusId, String code);
    
    boolean existsByCampusIdAndName(Long campusId, String name);
    
    @Query("SELECT COUNT(b) FROM Building b WHERE b.campus.id = :campusId")
    Long countByCampusId(Long campusId);
} 