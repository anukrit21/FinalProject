package com.demoApp.owner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demoApp.owner.entity.Mess;
import com.demoApp.owner.entity.Owner;

import java.util.List;

@Repository
public interface MessRepository extends JpaRepository<Mess, Long> {
    List<Mess> findByOwner(Owner owner);
    List<Mess> findByOwnerAndAvailability(Owner owner, boolean availability);
    List<Mess> findByAvailability(boolean availability);
    List<Mess> findByFoodNameContainingIgnoreCase(String foodName);
} 