package com.demoApp.mess.controller;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.mess.dto.MessOrderDTO;
import com.demoApp.mess.entity.Mess;
import com.demoApp.mess.entity.MessOrder;
import com.demoApp.mess.entity.OrderItem;
import com.demoApp.mess.repository.MessOrderRepository;
import com.demoApp.mess.repository.MessRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessOrderService {

    private final MessOrderRepository orderRepository;
    private final MessRepository messRepository;
    public MessOrderService(MessOrderRepository orderRepository, 
                           MessRepository messRepository,
                           ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.messRepository = messRepository;
    }

    public List<MessOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public MessOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
    }

    public List<MessOrder> getOrdersByMessId(Long messId) {
        return orderRepository.findByMessId(messId);
    }

    public List<MessOrder> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<MessOrder> getUserOrdersForMess(Long userId, Long messId) {
        return orderRepository.findByUserIdAndMessId(userId, messId);
    }

    public List<MessOrder> getOrdersByStatus(Long messId, MessOrder.OrderStatus status) {
        return orderRepository.findByMessIdAndStatus(messId, status);
    }

    public List<MessOrder> getOrdersForPeriod(Long messId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByMessIdAndOrderDateBetween(messId, startDate, endDate);
    }

    @Transactional
    public MessOrder createOrder(MessOrderDTO orderDTO) {
        Mess mess = messRepository.findById(orderDTO.getMessId())
                .orElseThrow(() -> new EntityNotFoundException("Mess not found with ID: " + orderDTO.getMessId()));

        MessOrder order = new MessOrder();
        order.setUserId(orderDTO.getUserId());
        order.setMess(mess);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(MessOrder.OrderStatus.PENDING);
        order.setTotalAmount(orderDTO.getTotalAmount());

        List<OrderItem> items = orderDTO.getOrderItems().stream()
                .map(itemDTO -> {
                    OrderItem item = new OrderItem();
                    item.setMenuItemId(itemDTO.getMenuItemId());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setOrder(order);
                    return item;
                })
                .collect(Collectors.toList());

        order.setOrderItems(items);
        return orderRepository.save(order);
    }

    @Transactional
    public MessOrder updateOrderStatus(Long id, MessOrder.OrderStatus status) {
        MessOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        MessOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
        
        order.setStatus(MessOrder.OrderStatus.CANCELED);
        orderRepository.save(order);
    }
}