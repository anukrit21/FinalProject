package com.demoApp.delivery.repository;

import com.demoApp.delivery.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    Optional<DeliveryPerson> findByEmail(String email);
    Optional<DeliveryPerson> findByMobileNumber(String mobileNumber);
    List<DeliveryPerson> findByIsAvailableTrue();
    List<DeliveryPerson> findByDeliveryZone(DeliveryPerson.DeliveryZone zone);
    List<DeliveryPerson> findByDeliveryZoneAndIsAvailableTrue(DeliveryPerson.DeliveryZone zone);
    List<DeliveryPerson> findByStatus(DeliveryPerson.DeliveryPersonStatus status);
    
    @Query("SELECT dp FROM DeliveryPerson dp WHERE dp.isAvailable = true AND dp.rating >= :minRating ORDER BY dp.rating DESC")
    List<DeliveryPerson> findAvailableDeliveryPersonsWithMinRating(double minRating);
    
    @Query("SELECT dp FROM DeliveryPerson dp WHERE dp.isAvailable = true AND dp.deliveryZone = :zone AND dp.rating >= :minRating ORDER BY dp.rating DESC")
    List<DeliveryPerson> findAvailableDeliveryPersonsInZoneWithMinRating(DeliveryPerson.DeliveryZone zone, double minRating);
} 