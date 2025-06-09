package io.farmx.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.farmx.enums.OrderStatus;

@Entity
@Table(name = "farm_orders")
public class FarmOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OrderStatus farmOrderStatus; // ex: "PENDING", "READY", "DELIVERED"

    private LocalDateTime deliveryTime;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    

    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;


    @OneToMany(mappedBy = "farmOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public LocalDateTime getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(LocalDateTime deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Farm getFarm() {
		return farm;
	}

	public void setFarm(Farm farm) {
		this.farm = farm;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public OrderStatus getOrderStatus() {
		return farmOrderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.farmOrderStatus = orderStatus;
	}
    
    
}
