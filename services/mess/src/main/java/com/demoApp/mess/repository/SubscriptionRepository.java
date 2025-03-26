package com.demoApp.mess.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demoApp.mess.entity.Subscription;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByMessId(Long messId);
    List<Subscription> findByMessIdAndType(Long messId, Subscription.SubscriptionType type);
    List<Subscription> findByMessIdAndActive(Long messId, boolean active);
    Page<Subscription> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT s FROM Subscription s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND s.active = true")
    Page<Subscription> searchActiveSubscriptions(String keyword, Pageable pageable);
    
    @Query("SELECT s FROM Subscription s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND s.mess.id = :messId " +
           "AND s.active = true")
    Page<Subscription> searchActiveSubscriptionsByMessId(String keyword, Long messId, Pageable pageable);
}
