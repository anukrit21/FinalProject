package com.demoApp.delivery.repository;

import com.demoApp.delivery.entity.Delivery;
import com.demoApp.delivery.entity.DeliveryPerson;
import com.demoApp.delivery.entity.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByDeliveryPerson(DeliveryPerson deliveryPerson);
    List<Delivery> findByDeliveryPersonId(Long deliveryPersonId);
    List<Delivery> findByUserId(Long userId);
    List<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
    List<Delivery> findByDeliveryType(Delivery.DeliveryType deliveryType);
    
    // ✅ Add missing method
    List<Delivery> findByPickupPoint(PickupPoint pickupPoint);

    @Query("SELECT d FROM Delivery d WHERE d.scheduledTime BETWEEN :startTime AND :endTime")
    List<Delivery> findByScheduledTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT d FROM Delivery d WHERE d.deliveryPerson.id = :deliveryPersonId AND d.status = :status")
    List<Delivery> findByDeliveryPersonIdAndStatus(Long deliveryPersonId, Delivery.DeliveryStatus status);
    
    @Query("SELECT d FROM Delivery d WHERE d.userId = :userId AND d.status IN :statuses")
    List<Delivery> findByUserIdAndStatusIn(Long userId, List<Delivery.DeliveryStatus> statuses);
    
    @Query("SELECT d FROM Delivery d WHERE d.deliveryType = :deliveryType AND d.scheduledTime BETWEEN :startTime AND :endTime")
    List<Delivery> findByDeliveryTypeAndScheduledTimeBetween(Delivery.DeliveryType deliveryType, LocalDateTime startTime, LocalDateTime endTime);
}
