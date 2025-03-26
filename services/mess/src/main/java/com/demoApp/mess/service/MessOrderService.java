package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.mess.dto.MessOrderDTO;
import com.demoApp.mess.entity.MessOrder;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.MessOrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessOrderService {

    private final MessOrderRepository messOrderRepository;
    private final ModelMapper modelMapper;

    public List<MessOrder> getAllOrders() {
        return messOrderRepository.findAll();
    }

    public MessOrder getOrderById(Long id) {
        return messOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<MessOrder> getOrdersByMessId(Long messId) {
        return messOrderRepository.findByMessId(messId);
    }

    public List<MessOrder> getOrdersByStatus(Long messId, MessOrder.OrderStatus status) {
        return messOrderRepository.findByMessIdAndStatus(messId, status);
    }

    @Transactional
    public MessOrder createOrder(MessOrderDTO messOrderDTO) {
        MessOrder order = modelMapper.map(messOrderDTO, MessOrder.class);
        order.setOrderDate(LocalDateTime.now());
        return messOrderRepository.save(order);
    }

    @Transactional
    public MessOrder updateOrderStatus(Long id, MessOrder.OrderStatus status) {
        MessOrder order = messOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        return messOrderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        MessOrder order = messOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(MessOrder.OrderStatus.CANCELED);
        messOrderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        MessOrder order = messOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        messOrderRepository.delete(order);
    }

    public List<MessOrder> getOrdersForPeriod(Long messId, LocalDateTime startDate, LocalDateTime endDate) {
        return messOrderRepository.findByMessIdAndOrderDateBetween(messId, startDate, endDate);
    }

    public List<MessOrder> getUserOrders(Long userId) {
        return messOrderRepository.findByUserId(userId);
    }

    public List<MessOrder> getUserOrdersForMess(Long userId, Long messId) {
        return messOrderRepository.findByUserIdAndMessId(userId, messId);
    }
}