package io.farmx.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.farmx.enums.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalAmount;

    private OrderStatus orderStatus; // ex: "PENDING", "READY", "DELIVERED"
    private LocalDateTime createdAt;

    private String deliveryCode;
    private LocalDateTime deliveryCodeExpiresAt;
    private LocalDateTime deliveredAt;

    private LocalDateTime estimatedDeliveryTime;
    @ManyToOne
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FarmOrder> farmOrders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "handler_id")
    private Handler handler;

    public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public List<FarmOrder> getFarmOrders() {
		return farmOrders;
	}

	public void setFarmOrders(List<FarmOrder> farmOrders) {
		this.farmOrders = farmOrders;
	}


	public LocalDateTime getEstimatedDeliveryTime() {
		return estimatedDeliveryTime;
	}

	public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
		this.estimatedDeliveryTime = estimatedDeliveryTime;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public LocalDateTime getDeliveryCodeExpiresAt() {
		return deliveryCodeExpiresAt;
	}

	public void setDeliveryCodeExpiresAt(LocalDateTime deliveryCodeExpiresAt) {
		this.deliveryCodeExpiresAt = deliveryCodeExpiresAt;
	}

	public LocalDateTime getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(LocalDateTime deliveredAt) {
		this.deliveredAt = deliveredAt;
	}

      
}
