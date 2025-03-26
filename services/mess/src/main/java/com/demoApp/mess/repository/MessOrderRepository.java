package com.demoApp.mess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demoApp.mess.entity.MessOrder;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessOrderRepository extends JpaRepository<MessOrder, Long> {
    List<MessOrder> findByMessId(Long messId);
    List<MessOrder> findByUserId(Long userId);
    List<MessOrder> findByUserIdAndMessId(Long userId, Long messId);
    List<MessOrder> findByMessIdAndStatus(Long messId, MessOrder.OrderStatus status);
    List<MessOrder> findByMessIdAndOrderDateBetween(Long messId, LocalDateTime startDate, LocalDateTime endDate);
}
