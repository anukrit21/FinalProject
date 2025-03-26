package com.demoApp.delivery.repository;

import com.demoApp.delivery.entity.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupPointRepository extends JpaRepository<PickupPoint, Long> {
    List<PickupPoint> findByCampusZone(PickupPoint.CampusZone campusZone);
    List<PickupPoint> findByIsActiveTrue();
    List<PickupPoint> findByCampusZoneAndIsActiveTrue(PickupPoint.CampusZone campusZone);
} 