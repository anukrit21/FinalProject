package com.demoApp.owner.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.owner.dto.OrderDTO;
import com.demoApp.owner.dto.OrderResponse;
import com.demoApp.owner.entity.MenuItem;
import com.demoApp.owner.entity.Order;
import com.demoApp.owner.entity.Owner;
import com.demoApp.owner.exception.ResourceNotFoundException;
import com.demoApp.owner.repository.MenuItemRepository;
import com.demoApp.owner.repository.OrderRepository;
import com.demoApp.owner.repository.OwnerRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
//awaz break ho rha hai haa haa cursor editor ok okk user tu kar nhi smajh rha haiok 1 hour okk ok
    private final OrderRepository orderRepository;
    private final OwnerRepository ownerRepository;
    private final MenuItemRepository menuItemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public OrderResponse createOrder(OrderDTO orderDTO) {
        Owner owner = ownerRepository.findById(orderDTO.getOwnerId())
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + orderDTO.getOwnerId()));

        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setOwner(owner);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);

        List<Order.OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Loop over each OrderItemDTO in the incoming OrderDTO
        for (com.demoApp.owner.dto.OrderDTO.OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemDTO.getMenuItemId()));

            int quantity = itemDTO.getQuantity();
            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);

            Order.OrderItem orderItem = new Order.OrderItem();
                orderItem.setMenuItemId(menuItem.getId());
                orderItem.setName(menuItem.getName());
                orderItem.setPrice(menuItem.getPrice());
                orderItem.setQuantity(quantity);
                orderItem.setItemTotal(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(order -> modelMapper.map(order, OrderResponse.class))
            .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return modelMapper.map(order, OrderResponse.class);
    }

    public List<OrderResponse> getOrdersByOwnerId(Long ownerId) {
        return orderRepository.findByOwnerId(ownerId).stream()
            .map(order -> modelMapper.map(order, OrderResponse.class))
            .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderResponse.class);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
    }
}
