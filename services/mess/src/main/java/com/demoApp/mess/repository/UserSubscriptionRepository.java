package com.demoApp.mess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demoApp.mess.entity.UserSubscription;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    
    List<UserSubscription> findByUserId(Long userId);
    
    List<UserSubscription> findByUserIdAndStatus(Long userId, UserSubscription.SubscriptionStatus status);
    
    @Query("SELECT us FROM UserSubscription us WHERE us.userId = :userId AND us.status = 'ACTIVE' AND us.endDate >= :today")
    List<UserSubscription> findActiveSubscriptionsByUserId(Long userId, LocalDate today);
    
    List<UserSubscription> findBySubscriptionMessId(Long messId);
    
    List<UserSubscription> findBySubscriptionMessIdAndStatus(Long messId, UserSubscription.SubscriptionStatus status);
    
    @Query("SELECT us FROM UserSubscription us WHERE us.subscription.messId = :messId AND us.status = 'ACTIVE' AND us.endDate >= :today")
    List<UserSubscription> findActiveSubscriptionsByMessId(Long messId, LocalDate today);
    
    @Query("SELECT us FROM UserSubscription us WHERE us.status = 'ACTIVE' AND us.endDate < :today")
    List<UserSubscription> findExpiredSubscriptions(LocalDate today);
} 