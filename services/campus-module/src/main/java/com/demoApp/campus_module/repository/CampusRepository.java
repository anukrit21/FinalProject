package com.demoApp.campus_module.repository;

import com.demoApp.campus_module.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    
    List<Campus> findByActive(boolean active);
    
    List<Campus> findByCampusType(Campus.CampusType campusType);
    
    List<Campus> findByCampusTypeAndActive(Campus.CampusType campusType, boolean active);
    
    List<Campus> findByCity(String city);
    
    List<Campus> findByState(String state);
    
    List<Campus> findByCountry(String country);
    
    @Query("SELECT c FROM Campus c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Campus> searchByKeyword(String keyword);
    
    @Query("SELECT DISTINCT c.city FROM Campus c WHERE c.country = :country")
    List<String> findDistinctCitiesByCountry(String country);
    
    @Query("SELECT DISTINCT c.state FROM Campus c WHERE c.country = :country")
    List<String> findDistinctStatesByCountry(String country);
    
    @Query("SELECT DISTINCT c.campusType FROM Campus c")
    List<Campus.CampusType> findDistinctCampusTypes();
    
    boolean existsByNameAndAddress(String name, String address);
} 