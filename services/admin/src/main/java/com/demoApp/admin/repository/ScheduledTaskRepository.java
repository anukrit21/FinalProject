package com.demoApp.admin.repository;

import com.demoApp.admin.entity.ScheduledTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
    
    Optional<ScheduledTask> findByTaskNameAndServiceName(String taskName, String serviceName);
    
    List<ScheduledTask> findByServiceName(String serviceName);
    
    List<ScheduledTask> findByStatus(ScheduledTask.Status status);
    
    List<ScheduledTask> findByNextExecutionTimeBefore(LocalDateTime time);
    
    Page<ScheduledTask> findByServiceNameOrderByNextExecutionTimeAsc(String serviceName, Pageable pageable);
    
    Page<ScheduledTask> findByStatusOrderByNextExecutionTimeAsc(ScheduledTask.Status status, Pageable pageable);
} 