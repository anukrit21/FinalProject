package com.demoApp.owner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.demoApp.owner.entity.Order;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOwnerId(Long ownerId);
    List<Order> findByOwnerIdAndStatus(Long ownerId, Order.OrderStatus status);
    List<Order> findByOwnerIdAndOrderDateBetween(Long ownerId, LocalDateTime start, LocalDateTime end);
}
