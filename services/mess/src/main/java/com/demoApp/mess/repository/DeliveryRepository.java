package com.demoApp.mess.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demoApp.mess.entity.Delivery;
import com.demoApp.mess.entity.DeliveryPerson;
import com.demoApp.mess.entity.User;
import com.demoApp.mess.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    // Find delivery by tracking code
    Optional<Delivery> findByTrackingCode(String trackingCode);
    
    // Find deliveries by customer
    List<Delivery> findByCustomer(User customer);
    Page<Delivery> findByCustomer(User customer, Pageable pageable);
    
    // Find deliveries by mess
    List<Delivery> findByMess(User mess);
    Page<Delivery> findByMess(User mess, Pageable pageable);
    
    // Find deliveries by delivery person
    List<Delivery> findByDeliveryPerson(DeliveryPerson deliveryPerson);
    Page<Delivery> findByDeliveryPerson(DeliveryPerson deliveryPerson, Pageable pageable);
    
    // Find deliveries by status
    List<Delivery> findByStatus(DeliveryStatus status);
    Page<Delivery> findByStatus(DeliveryStatus status, Pageable pageable);
    
    // Find deliveries by customer and status
    List<Delivery> findByCustomerAndStatus(User customer, DeliveryStatus status);
    Page<Delivery> findByCustomerAndStatus(User customer, DeliveryStatus status, Pageable pageable);
    
    // Find deliveries by mess and status
    List<Delivery> findByMessAndStatus(User mess, DeliveryStatus status);
    Page<Delivery> findByMessAndStatus(User mess, DeliveryStatus status, Pageable pageable);
    
    // Find deliveries by delivery person and status
    List<Delivery> findByDeliveryPersonAndStatus(DeliveryPerson deliveryPerson, DeliveryStatus status);
    Page<Delivery> findByDeliveryPersonAndStatus(DeliveryPerson deliveryPerson, DeliveryStatus status, Pageable pageable);
    
    // Find deliveries by order reference
    List<Delivery> findByOrderReferenceIdAndOrderReferenceType(Long orderReferenceId, String orderReferenceType);
    
    // Find deliveries scheduled for a specific date range
    List<Delivery> findByScheduledDeliveryTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // Find unassigned deliveries (pending)
    List<Delivery> findByStatusAndDeliveryPersonIsNull(DeliveryStatus status);
    
    // Find deliveries that are late (scheduled delivery time is in the past but status is not DELIVERED or FAILED)
    @Query("SELECT d FROM Delivery d WHERE d.scheduledDeliveryTime < :now " +
           "AND d.status NOT IN ('DELIVERED', 'FAILED', 'CANCELLED')")
    List<Delivery> findLateDeliveries(LocalDateTime now);
    
    // Get delivery count by status for a specific mess
    @Query("SELECT d.status, COUNT(d) FROM Delivery d WHERE d.mess = :mess GROUP BY d.status")
    List<Object[]> countDeliveriesByStatusForMess(User mess);
    
    // Get delivery count by day for the last N days for a specific mess
    @Query(value = "SELECT DATE(d.created_at) as delivery_date, COUNT(d.id) " +
                   "FROM deliveries d " +
                   "WHERE d.mess_id = :messId " +
                   "AND d.created_at >= :startDate " +
                   "GROUP BY DATE(d.created_at) " +
                   "ORDER BY delivery_date", 
           nativeQuery = true)
    List<Object[]> countDeliveriesByDayForMess(Long messId, LocalDateTime startDate);
} 