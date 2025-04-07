package com.demoApp.mess.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String itemName;
    
    private String itemDescription;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private BigDecimal totalPrice;
    
    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;


public void setMenuId(Long menuId) {
    this.menu = new Menu();
    this.menu.setId(menuId);
}


@ManyToOne
@JoinColumn(name = "order_id")
private MessOrder order;

public void setOrder(MessOrder order) {
    this.order = order;
}

}