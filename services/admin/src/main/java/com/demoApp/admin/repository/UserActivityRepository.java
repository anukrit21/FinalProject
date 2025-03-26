package com.demoApp.admin.repository;

import com.demoApp.admin.entity.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    Page<UserActivity> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
    
    List<UserActivity> findByUserId(Long userId);
    
    Page<UserActivity> findByActivityTypeOrderByTimestampDesc(String userAction, Pageable pageable);
    
    Page<UserActivity> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<UserActivity> findByServiceNameOrderByTimestampDesc(String serviceName, Pageable pageable);
    
} 